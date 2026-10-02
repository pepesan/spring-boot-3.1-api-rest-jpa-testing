package com.inetum.demo.dtos.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Perro;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PerroDTO extends AnimalDTO<Perro> {
    @NotBlank
    private String raza;

    private boolean adiestrado;

    @Override
    public void applyTo(Perro perro) {
        applyCommon(perro);
        perro.setRaza(raza);
        perro.setAdiestrado(adiestrado);
    }
}
