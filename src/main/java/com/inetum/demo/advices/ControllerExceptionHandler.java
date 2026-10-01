package com.inetum.demo.advices;

import com.inetum.demo.dtos.ErrorResponseDto;
import com.inetum.demo.gateways.AlumnoGatewayException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    private static final String ERROR_TYPE_BASE = "https://inetum.com/errors/";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    /** Valor de Retry-After del 503 del gateway (alumnos.gateway.retry-after). */
    @Value("${alumnos.gateway.retry-after:5s}")
    private Duration retryAfter = Duration.ofSeconds(5);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "resource-not-found", "Recurso no encontrado",
                ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()
                .forEach(ge -> errors.putIfAbsent(ge.getObjectName(), ge.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "validation-error", "Error de validación",
                "La petición contiene datos no válidos", request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v ->
                errors.putIfAbsent(v.getPropertyPath().toString(), v.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "validation-error", "Error de validación",
                "La petición contiene datos no válidos", request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "malformed-request", "Petición mal formada",
                "El cuerpo de la petición no se puede interpretar", request, null);
    }

    /**
     * Fallo del servicio remoto tras agotar los reintentos del gateway: 503 + Retry-After si es
     * transitorio (el cliente puede reintentar más tarde), 502 si el remoto rechazó la petición.
     */
    @ExceptionHandler(AlumnoGatewayException.class)
    public ResponseEntity<ErrorResponseDto> handleGateway(
            AlumnoGatewayException ex, HttpServletRequest request) {
        String instance = resolveInstance(request);
        log.warn("APP: fallo del gateway de alumnos [{}]: {}", instance, ex.getMessage());
        if (!ex.isTransitorio()) {
            return build(HttpStatus.BAD_GATEWAY, "upstream-rejected", "Petición rechazada por el servicio remoto",
                    "El servicio remoto de alumnos rechazó la petición", instance, null);
        }
        ResponseEntity<ErrorResponseDto> base = build(HttpStatus.SERVICE_UNAVAILABLE, "upstream-unavailable",
                "Servicio remoto no disponible",
                "El servicio remoto de alumnos no responde; inténtelo de nuevo más tarde", instance, null);
        return ResponseEntity.status(base.getStatusCode())
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfter.toSeconds()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(base.getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex, HttpServletRequest request) {
        // Excepciones estándar de Spring MVC (405, 415, 404 de recurso...) conservan su código HTTP
        if (ex instanceof ErrorResponse er) {
            HttpStatusCode status = er.getStatusCode();
            HttpStatus resolved = HttpStatus.resolve(status.value());
            return build(status, "http-" + status.value(),
                    resolved != null ? resolved.getReasonPhrase() : "Error",
                    er.getBody().getDetail(), request, null);
        }
        String instance = resolveInstance(request);
        // El detalle real solo va al log, nunca al cliente
        log.error("APP: error inesperado [{}]", instance, ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "internal-error", "Error interno del servidor",
                "Se ha producido un error inesperado", instance, null);
    }

    private ResponseEntity<ErrorResponseDto> build(HttpStatusCode status, String typeSlug, String title,
                                                   String detail, HttpServletRequest request,
                                                   Map<String, String> errors) {
        return build(status, typeSlug, title, detail, resolveInstance(request), errors);
    }

    private String resolveInstance(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        return requestId != null && !requestId.isBlank() ? requestId : UUID.randomUUID().toString();
    }

    private ResponseEntity<ErrorResponseDto> build(HttpStatusCode status, String typeSlug, String title,
                                                   String detail, String instance,
                                                   Map<String, String> errors) {
        ErrorResponseDto body = ErrorResponseDto.builder()
                .type(ERROR_TYPE_BASE + typeSlug)
                .title(title)
                .status(status.value())
                .detail(detail)
                .instance(instance)
                .timestamp(Instant.now())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }
}
