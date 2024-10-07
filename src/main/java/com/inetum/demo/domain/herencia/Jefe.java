package com.inetum.demo.domain.herencia;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "EmpId")
public class Jefe extends Empleado {
    private String departamento;
}
