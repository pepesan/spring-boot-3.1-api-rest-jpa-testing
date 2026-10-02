package com.inetum.demo.domain.onetomany;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;

    // Lado inverso: al serializar la persona dentro de una direccion se ignora su lista
    // "addresses" para cortar el ciclo (ver comentario en Person.addresses sobre por que
    // no se usa @JsonIdentityInfo).
    // "hibernateLazyInitializer" y "handler" son propiedades internas del proxy que Hibernate
    // crea para esta relacion LAZY (person no se carga hasta que se accede a ella). Jackson
    // las veria como getters del proxy y fallaria al serializarlas (o las incluiria en el
    // JSON), asi que se ignoran. Person ya las ignora a nivel de clase; se repiten aqui
    // por seguridad, para no depender de como Jackson combine ambas anotaciones.
    @JsonIgnoreProperties({"addresses", "hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;
}
