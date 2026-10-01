package com.inetum.demo.gateways;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;

import java.util.List;
import java.util.Optional;

/**
 * Patrón Gateway: punto único de acceso al servicio remoto de alumnos. El resto de la aplicación
 * no sabe si hay HTTP, reintentos o timeouts detrás; solo ve estas operaciones y
 * {@link AlumnoGatewayException}.
 */
public interface AlumnoGateway {
    List<Alumno> findAll();

    Optional<Alumno> findById(Long id);

    Alumno create(AlumnoDTO alumno);

    Optional<Alumno> delete(Long id);
}
