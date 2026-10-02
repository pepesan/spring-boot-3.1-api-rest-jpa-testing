package com.inetum.demo.domain.herencia.singletable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Herencia SINGLE_TABLE: Vehiculo, Coche, Moto y Camion viven en una única tabla "vehiculos".
 * <ul>
 *   <li>La columna discriminadora {@code tipo_vehiculo} indica a qué subclase pertenece cada fila.</li>
 *   <li>Las columnas de cada subclase quedan en la misma tabla y valen NULL en las filas de las demás,
 *       por eso los campos específicos usan tipos envoltorio (Integer, Double...) y no primitivos.</li>
 *   <li>Ventaja: consultas polimórficas sin JOIN. Inconveniente: columnas casi vacías y sin NOT NULL
 *       posible en los campos de las subclases (frente a JOINED, como Ordenador/Portatil).</li>
 * </ul>
 * La clase es abstracta: solo se persisten Coche, Moto o Camion. {@code @JsonTypeInfo} añade el
 * campo "tipo" al JSON para distinguirlos al listar vehículos de forma polimórfica.
 */
@Data
@Entity
@Table(name = "vehiculos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_vehiculo", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Coche.class, name = "COCHE"),
        @JsonSubTypes.Type(value = Moto.class, name = "MOTO"),
        @JsonSubTypes.Type(value = Camion.class, name = "CAMION")
})
public abstract class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String marca;
    private String modelo;
    private int anio;
}
