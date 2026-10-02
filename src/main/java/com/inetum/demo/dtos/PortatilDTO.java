package com.inetum.demo.dtos;

import com.inetum.demo.domain.herencia.Portatil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/** Datos de entrada de un Portatil (POST/PUT): campos de Ordenador (marca, modelo) y propios. */
@Data
public class PortatilDTO {
    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    /** Peso en kg. */
    @Positive
    private double peso;

    /** Duración de la batería en horas. */
    @PositiveOrZero
    private int duracionBateria;

    /** Vuelca este DTO sobre la entidad; el id nunca se toca (lo gestiona la BBDD / la URL). */
    public void applyTo(Portatil portatil) {
        portatil.setMarca(marca);
        portatil.setModelo(modelo);
        portatil.setPeso(peso);
        portatil.setDuracionBateria(duracionBateria);
    }
}
