package com.inetum.demo.domain.herencia;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "PerId")
public class Empleado extends Persona {
    private String dni;
    private double sueldo;
}
