package com.inetum.demo.patch;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Aplica un JSON Merge Patch (RFC 7386) sobre un objeto y valida el resultado.
 * Los campos ausentes del parche no se tocan y los campos desconocidos se ignoran.
 */
@Component
public class MergePatchService {
    private final JsonMapper mapper;
    private final Validator validator;

    public MergePatchService(JsonMapper mapper, Validator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    /**
     * @param copia objeto sobre el que se aplica el parche: debe ser una COPIA, nunca el
     *              objeto guardado, para no modificarlo si el resultado no es válido
     * @throws ConstraintViolationException si el objeto resultante no cumple sus validaciones
     */
    public <T> T apply(T copia, JsonNode parche) {
        T parcheado = mapper.readerForUpdating(copia).readValue(parche);
        var violaciones = validator.validate(parcheado);
        if (!violaciones.isEmpty()) {
            throw new ConstraintViolationException(violaciones);
        }
        return parcheado;
    }
}
