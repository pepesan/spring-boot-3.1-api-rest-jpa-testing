package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Ordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface OrdenadorRepository extends JpaRepository<Ordenador, Long> {
}
