package com.inetum.demo.repositories.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Sobremesa;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase hace JOIN con las tablas de sus padres; por Ordenador, devuelve todas. */
public interface SobremesaRepository extends JpaRepository<Sobremesa, Long> {}
