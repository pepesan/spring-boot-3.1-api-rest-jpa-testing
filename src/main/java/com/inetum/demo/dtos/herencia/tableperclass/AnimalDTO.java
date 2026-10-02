package com.inetum.demo.dtos.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Animal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Campos comunes de entrada (POST/PUT) de cualquier animal; cada subclase añade los suyos. */
@Data
public abstract class AnimalDTO<A extends Animal> {
    @NotBlank
    private String nombre;

    @Min(0)
    private int edad;

    /** Vuelca el DTO sobre la entidad; el id nunca se toca. */
    public abstract void applyTo(A animal);

    protected void applyCommon(Animal animal) {
        animal.setNombre(nombre);
        animal.setEdad(edad);
    }
}
