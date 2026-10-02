package com.inetum.demo.gateways;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import tools.jackson.databind.JsonNode;

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

    /** Sustitución completa (PUT). @return el alumno modificado, o vacío si no existía */
    Optional<Alumno> update(Long id, AlumnoDTO alumno);

    /** Modificación parcial (PATCH application/merge-patch+json). @return el alumno modificado, o vacío si no existía */
    Optional<Alumno> patch(Long id, JsonNode patch);

    Optional<Alumno> delete(Long id);
}
