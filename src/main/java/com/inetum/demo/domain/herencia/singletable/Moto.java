package com.inetum.demo.domain.herencia.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MOTO")
public class Moto extends Vehiculo {
    private Integer cilindrada;
    private Boolean tieneSidecar;
}
