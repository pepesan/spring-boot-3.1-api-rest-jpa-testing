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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Evita el ciclo Person -> addresses -> person -> addresses...
    // Antes se usaba @JsonIdentityInfo, que serializa cada objeto completo solo la primera vez
    // y despues lo sustituye por su id (ej. "person": 1, o un "2" suelto en la lista),
    // dando un JSON inconsistente segun desde donde se empiece a serializar.
    // Aqui se corta el ciclo ignorando la propiedad inversa "person" en cada direccion.
    @JsonIgnoreProperties("person")
    @OneToMany(mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Address2> addresses = new ArrayList<>();


}
