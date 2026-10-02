package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Portatil;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PortatilDTO extends OrdenadorDTO<Portatil> {
    /** Peso en kg. */
    @Positive
    private double peso;

    /** Duración de la batería en horas. */
    @PositiveOrZero
    private int duracionBateria;

    @Override
    public void applyTo(Portatil portatil) {
        applyCommon(portatil);
        portatil.setPeso(peso);
        portatil.setDuracionBateria(duracionBateria);
    }
}
