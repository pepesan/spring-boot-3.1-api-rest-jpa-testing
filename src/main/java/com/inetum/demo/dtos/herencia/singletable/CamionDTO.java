package com.inetum.demo.dtos.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Camion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CamionDTO extends VehiculoDTO<Camion> {
    @Positive
    private double cargaMaximaKg;

    @Min(2)
    private int numeroEjes;

    @Override
    public void applyTo(Camion camion) {
        applyCommon(camion);
        camion.setCargaMaximaKg(cargaMaximaKg);
        camion.setNumeroEjes(numeroEjes);
    }
}
