package com.inetum.demo.domain.herencia;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity

/*
    Tenemos tres estrategias:
    - Table per class: cada entidad tiene su propia tabla
    con los campos comunes y sus específicos
    - Joined Table: Hay una tabla por cada entidad pero
    la tabla madre tiene los campos comunes de la
    entidad común y las tablas hijas los específicos de las
    entidades que heredan, se usa una FK para enlazar con la
    tabla madre y sus datos relacionados
    - Single Table: todas las Entidades en una misma tabla
    se usa un discriminador para distinguir entre registros
    de cada Entidad

 */

// Joined Table
@Inheritance(strategy = InheritanceType.JOINED)

// Table per class
// @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

// Single Table: para que todas las clases en una misma tabla
// con discriminador
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// @DiscriminatorColumn(name = "tipo_ordenador")

public class Ordenador {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String marca;
    private String modelo;
}
