package com.inetum.demo.repositories.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Moto;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase añade solo el filtro por discriminador; por Vehiculo, devuelve todas. */
public interface MotoRepository extends JpaRepository<Moto, Long> {
}
