package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Empleado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Campos de Empleado compartidos por EmpleadoDTO y JefeDTO (un Jefe es un Empleado). */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EmpleadoBaseDTO<E extends Empleado> extends PersonaDTO<E> {
    @NotBlank
    private String dni;

    @PositiveOrZero
    private double sueldo;

    @Override
    public void applyTo(E empleado) {
        applyCommon(empleado);
        empleado.setDni(dni);
        empleado.setSueldo(sueldo);
    }
}
