package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Ordenador;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Campos comunes de entrada (POST/PUT) de cualquier ordenador; cada subclase añade los suyos. */
@Data
public abstract class OrdenadorDTO<O extends Ordenador> {
    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    /** Vuelca el DTO sobre la entidad; el id nunca se toca. */
    public abstract void applyTo(O ordenador);

    protected void applyCommon(Ordenador ordenador) {
        ordenador.setMarca(marca);
        ordenador.setModelo(modelo);
    }
}
