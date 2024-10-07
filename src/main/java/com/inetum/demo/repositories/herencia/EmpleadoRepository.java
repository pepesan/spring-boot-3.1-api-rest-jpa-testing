package com.inetum.demo.repositories.herencia;

import com.inetum.demo.domain.herencia.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findBySueldoGreaterThan(double sueldo);
}
