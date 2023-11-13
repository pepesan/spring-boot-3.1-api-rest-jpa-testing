package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Ordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(
        // define la ruta principal
        path = "ordenadores",
        // define el nombre de la entidad
        collectionResourceRel = "ordenadores",
        // define si se exportan los datos
        exported = false
)
public interface OrdenadorRepository extends JpaRepository<Ordenador, Long> {
}
