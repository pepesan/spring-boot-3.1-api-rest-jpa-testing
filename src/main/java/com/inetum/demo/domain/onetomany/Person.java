package com.inetum.demo.domain.onetomany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties()
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Evita el ciclo Person -> addresses -> person -> addresses...
    // POr defecto el @JsonIdentityInfo, serializa cada objeto completo solo la primera vez
    // y después lo sustituye por su id (ej. "person": 1, o un "2" suelto en la lista),
    // dando un JSON inconsistente según desde donde se empiece a serializar.
    // Aquí se corta el ciclo ignorando la propiedad inversa "person" en cada direccion.
    @JsonIgnoreProperties({"person", "hibernateLazyInitializer", "handler"} )
    @OneToMany(mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Address2> addresses = new ArrayList<>();


}
