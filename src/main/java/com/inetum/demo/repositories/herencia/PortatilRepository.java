package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Portatil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource
public interface PortatilRepository extends JpaRepository<Portatil, Long> {
}
