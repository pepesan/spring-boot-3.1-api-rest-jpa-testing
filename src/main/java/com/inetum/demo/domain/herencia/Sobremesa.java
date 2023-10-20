package com.inetum.demo.domain.herencia;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity

// estrategia con Joined table
@PrimaryKeyJoinColumn(name = "OrdId")
// si usamos Single Table debemos activar el discrimimador
// @DiscriminatorValue("sobremesa")
public class Sobremesa extends Ordenador {

    private String tipoTorre;
    private boolean tieneMonitor;
}
