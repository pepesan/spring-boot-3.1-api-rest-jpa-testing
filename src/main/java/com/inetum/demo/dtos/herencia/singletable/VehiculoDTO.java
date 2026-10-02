package com.inetum.demo.dtos.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Vehiculo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Campos comunes de entrada (POST/PUT) de cualquier vehículo; cada subclase añade los suyos. */
@Data
public abstract class VehiculoDTO<V extends Vehiculo> {
    @NotBlank
    private String marca;

    @NotBlank
    private String modelo;

    @Min(1900)
    private int anio;

    /** Vuelca el DTO sobre la entidad; el id nunca se toca. */
    public abstract void applyTo(V vehiculo);

    protected void applyCommon(Vehiculo vehiculo) {
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setAnio(anio);
    }
}
