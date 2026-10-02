package com.inetum.demo.domain.herencia.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "perros")
public class Perro extends Animal {
    private String raza;

    private boolean adiestrado;
}
