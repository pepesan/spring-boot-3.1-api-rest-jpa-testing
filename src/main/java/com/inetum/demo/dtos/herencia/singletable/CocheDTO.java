package com.inetum.demo.dtos.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Coche;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CocheDTO extends VehiculoDTO<Coche> {
    @Min(1)
    @Max(7)
    private int numeroPuertas;

    @NotBlank
    private String combustible;

    @Override
    public void applyTo(Coche coche) {
        applyCommon(coche);
        coche.setNumeroPuertas(numeroPuertas);
        coche.setCombustible(combustible);
    }
}
