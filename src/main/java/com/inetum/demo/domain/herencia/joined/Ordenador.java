package com.inetum.demo.domain.herencia.joined;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Herencia JOINED: Ordenador, Portatil y Sobremesa tienen cada uno su tabla.
 * <ul>
 *   <li>La tabla "ordenador" guarda los campos comunes (id, marca, modelo) y cada subclase una tabla
 *       con solo sus campos propios, enlazada por una FK a la clave primaria ({@code OrdId}).</li>
 *   <li>Como cada subclase tiene sus propias columnas, pueden ser NOT NULL y usar tipos primitivos
 *       (frente a SINGLE_TABLE, como Vehiculo/Coche).</li>
 *   <li>Ventaja: modelo normalizado. Inconveniente: las consultas leen con JOIN entre tablas.</li>
 * </ul>
 * La clase es abstracta: solo se persisten Portatil o Sobremesa. {@code @JsonTypeInfo} añade el
 * campo "tipo" al JSON para distinguirlos al listar ordenadores de forma polimórfica.
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Portatil.class, name = "PORTATIL"),
        @JsonSubTypes.Type(value = Sobremesa.class, name = "SOBREMESA")
})
public abstract class Ordenador {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String marca;

    private String modelo;
}
