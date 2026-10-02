package com.inetum.demo.domain.herencia.joined;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@PrimaryKeyJoinColumn(name = "OrdId")
public class Sobremesa extends Ordenador {
    private String tipoTorre;

    private boolean tieneMonitor;
}
