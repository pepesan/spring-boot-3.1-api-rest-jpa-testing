package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Sobremesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface SobremesaRepository extends JpaRepository<Sobremesa, Long> {
}
