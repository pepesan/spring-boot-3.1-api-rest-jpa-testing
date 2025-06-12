package com.inetum.demo.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEqualValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEqual {

    // El mensaje por defecto, {value} se sustituye por el parámetro
    String message() default "no debe ser igual a '{value}'";

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    // El texto prohibido
    String value() default "pepesan";
}

