package com.inetum.demo.domain.herencia.joined;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@PrimaryKeyJoinColumn(name = "OrdId")
public class Portatil extends Ordenador {
    private double peso;

    private int duracionBateria;
}
