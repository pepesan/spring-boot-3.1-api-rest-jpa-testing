package com.inetum.demo.repositories.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultar por una subclase añade solo el filtro por discriminador; por Vehiculo, devuelve todas. */
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
}
