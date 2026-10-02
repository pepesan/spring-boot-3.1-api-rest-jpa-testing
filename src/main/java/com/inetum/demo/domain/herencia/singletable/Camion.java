package com.inetum.demo.domain.herencia.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CAMION")
public class Camion extends Vehiculo {
    private Double cargaMaximaKg;
    private Integer numeroEjes;
}
