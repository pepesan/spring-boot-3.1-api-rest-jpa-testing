package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Persona;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Campos comunes de entrada (POST/PUT) de cualquier persona; cada subclase añade los suyos. */
@Data
public abstract class PersonaDTO<P extends Persona> {
    @NotBlank
    private String nombre;

    private String direccion;

    @Min(0)
    private Integer edad;

    /** Vuelca el DTO sobre la entidad; el id nunca se toca. */
    public abstract void applyTo(P persona);

    protected void applyCommon(Persona persona) {
        persona.setNombre(nombre);
        persona.setDireccion(direccion);
        persona.setEdad(edad);
    }
}
