package com.inetum.demo.domain.herencia.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("COCHE")
public class Coche extends Vehiculo {
    private Integer numeroPuertas;
    private String combustible;
}
