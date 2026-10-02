package com.inetum.demo.domain.herencia.tableperclass;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Herencia TABLE_PER_CLASS: cada subclase concreta (Perro, Gato) tiene su propia tabla completa, con
 * las columnas comunes de Animal repetidas más las suyas. No hay tabla compartida ni FK entre ellas.
 * <ul>
 *   <li>Como las tablas son independientes, el id no puede ser IDENTITY: se usa AUTO (secuencia
 *       compartida) para que un id no se repita entre perros y gatos.</li>
 *   <li>Ventaja: consultar un solo tipo no hace JOIN y cada tabla admite NOT NULL y primitivos.
 *       Inconveniente: la consulta polimórfica (todos los animales) usa UNION de las tablas.</li>
 * </ul>
 * La clase es abstracta: solo se persisten Perro o Gato. {@code @JsonTypeInfo} añade el campo "tipo"
 * al JSON para distinguirlos al listar animales de forma polimórfica.
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Perro.class, name = "PERRO"),
        @JsonSubTypes.Type(value = Gato.class, name = "GATO")
})
public abstract class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nombre;

    private int edad;
}
