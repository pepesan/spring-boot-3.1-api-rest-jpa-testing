package com.inetum.demo.repositories.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Jefe;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase hace JOIN con las tablas de sus padres; por Persona, devuelve todas. */
public interface JefeRepository extends JpaRepository<Jefe, Long> {}
