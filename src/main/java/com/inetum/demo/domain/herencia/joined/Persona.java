package com.inetum.demo.domain.herencia.joined;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Herencia JOINED en varios niveles: Persona -> Empleado -> Jefe, una tabla por clase.
 * Cada subclase enlaza con la tabla de su padre por FK ({@code PerId} en Empleado, {@code EmpId} en Jefe),
 * y consultar un Jefe hace JOIN con empleado y persona.
 * <p>
 * La clase es abstracta: solo se persisten Empleado o Jefe (que es un Empleado). {@code @JsonTypeInfo}
 * añade el campo "tipo" al JSON para distinguirlos al listar personas de forma polimórfica.
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Empleado.class, name = "EMPLEADO"),
        @JsonSubTypes.Type(value = Jefe.class, name = "JEFE")
})
public abstract class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String direccion;

    private Integer edad;
}
