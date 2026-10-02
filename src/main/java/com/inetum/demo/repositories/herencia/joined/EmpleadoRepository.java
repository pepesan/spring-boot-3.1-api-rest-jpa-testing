package com.inetum.demo.repositories.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Consultar por una subclase hace JOIN con las tablas de sus padres; por Persona, devuelve todas. */
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findBySueldoGreaterThan(double sueldo);
}
