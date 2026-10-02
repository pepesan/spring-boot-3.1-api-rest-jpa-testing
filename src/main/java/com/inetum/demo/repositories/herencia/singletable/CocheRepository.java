package com.inetum.demo.repositories.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Coche;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase añade solo el filtro por discriminador; por Vehiculo, devuelve todas. */
public interface CocheRepository extends JpaRepository<Coche, Long> {
}
