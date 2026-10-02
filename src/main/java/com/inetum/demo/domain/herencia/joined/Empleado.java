package com.inetum.demo.domain.herencia.joined;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@PrimaryKeyJoinColumn(name = "PerId")
public class Empleado extends Persona {
    private String dni;

    private double sueldo;
}
