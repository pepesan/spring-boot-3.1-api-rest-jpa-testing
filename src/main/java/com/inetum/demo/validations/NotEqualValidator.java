package com.inetum.demo.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEqualValidator implements ConstraintValidator<NotEqual, String> {

    private String forbidden;

    @Override
    public void initialize(NotEqual annotation) {
        // Guarda el valor que venga en la anotación
        this.forbidden = annotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Consideramos null como válido; si quieres null inválido, añade value != null
        if (value == null) {
            return true;
        }
        return !value.equals(forbidden);
    }
}

