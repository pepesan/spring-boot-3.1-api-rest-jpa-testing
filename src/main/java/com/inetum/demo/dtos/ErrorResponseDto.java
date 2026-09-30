package com.inetum.demo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Oculta los campos nulos en el JSON (ej. si no hay errores de validación)
public class ErrorResponseDto {

    private String type;                      // URI que identifica el tipo de error
    private String title;                     // Resumen corto del error
    private int status;                       // Código de estado HTTP (ej. 400, 500)
    private String detail;                    // Descripción detallada y segura para el cliente
    private String instance;                  // Identificador único de la solicitud (Correlation ID)
    private Instant timestamp;                // Momento exacto en que ocurrió el error

    // Opcional: Ideal para errores de validación de formularios (ej. {"email": "formato inválido"})
    private Map<String, String> errors;
}
