package com.inetum.demo.domain.herencia;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@PrimaryKeyJoinColumn(name = "OrdId")
// estrategia con Joined table
// si usamos Single table debemos activar el discrimimador
//@DiscriminatorValue("portatil")
public class Portatil extends Ordenador {
    private double peso;
    private int duracionBateria;
}
