package com.inetum.demo.repositories.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

/**
 * "Repositorio" cuya fuente de datos no es una base de datos sino las consultas HTTP
 * al endpoint {@code /api/v1/alumnos} (ver ApiAlumnoServiceController).
 */
public interface AlumnoApiRepository {
    List<Alumno> findAll();

    Optional<Alumno> findById(Long id);

    Alumno create(AlumnoDTO alumno);

    /** Sustitución completa (PUT). @return el alumno modificado, o vacío si no existía */
    Optional<Alumno> update(Long id, AlumnoDTO alumno);

    /** Modificación parcial (PATCH application/merge-patch+json). @return el alumno modificado, o vacío si no existía */
    Optional<Alumno> patch(Long id, JsonNode patch);

    /** @return el alumno borrado, o vacío si no existía */
    Optional<Alumno> delete(Long id);
}
