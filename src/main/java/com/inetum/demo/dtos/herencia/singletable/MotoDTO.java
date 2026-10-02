package com.inetum.demo.dtos.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Moto;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MotoDTO extends VehiculoDTO<Moto> {
    /** Cilindrada en cc. */
    @Positive
    private int cilindrada;

    private boolean tieneSidecar;

    @Override
    public void applyTo(Moto moto) {
        applyCommon(moto);
        moto.setCilindrada(cilindrada);
        moto.setTieneSidecar(tieneSidecar);
    }
}
