package com.inetum.demo.repositories.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Perro;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase lee solo su tabla; por Animal, hace UNION de todas. */
public interface PerroRepository extends JpaRepository<Perro, Long> {
}
