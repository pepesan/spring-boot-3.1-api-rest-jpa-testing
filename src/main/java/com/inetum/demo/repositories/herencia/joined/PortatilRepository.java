package com.inetum.demo.repositories.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Portatil;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase hace JOIN con las tablas de sus padres; por Ordenador, devuelve todas. */
public interface PortatilRepository extends JpaRepository<Portatil, Long> {}
