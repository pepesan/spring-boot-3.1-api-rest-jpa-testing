package com.inetum.demo.advices;

import com.inetum.demo.dtos.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorMessage>
    resourceNotFoundException(ResourceNotFoundException ex, WebRequest
            request) {
        ErrorMessage message = new ErrorMessage(
                404,
                new Date(),
                ex.getMessage(),
                "Error capturado por ResourceNotFoundException");
        return new ResponseEntity<ErrorMessage>(message,
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("APP: mensaje manejado");
        // Obtén los mensajes de error de la excepción
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // Puedes personalizar la respuesta según tus necesidades
        ErrorMessage apiError = new ErrorMessage(
                400,
                new Date(),
                errorMessage,
                "Error capturado por MethodArgumentNotValidException"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Devuelve la respuesta con el código de estado adecuado
        return new ResponseEntity<>(apiError, headers,HttpStatus.BAD_REQUEST);
    }
}
