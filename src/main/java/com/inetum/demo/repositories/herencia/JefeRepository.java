package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Jefe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface JefeRepository extends JpaRepository<Jefe, Long> {
}
