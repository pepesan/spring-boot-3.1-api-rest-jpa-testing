package com.inetum.demo.domain.manytomany;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    // Clave de igualdad de negocio. Con onlyExplicitlyIncluded = true y ningún campo incluido,
    // Lombok hace que todos los Role sean "iguales" y el Set<Role> de User descarta roles distintos.
    // unique + nullable=false: la BBDD rechaza roles duplicados o sin nombre (coherente con equals/hashCode por name)
    @EqualsAndHashCode.Include
    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER,
            mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
