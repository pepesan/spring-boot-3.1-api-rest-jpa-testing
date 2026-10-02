package com.inetum.demo.services.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

/** Casos de uso sobre alumnos obtenidos consumiendo el API REST por HTTP (no la base de datos). */
public interface AlumnoClienteService {
    List<Alumno> findAll();

    Optional<Alumno> findById(Long id);

    Alumno create(AlumnoDTO alumno);

    Optional<Alumno> update(Long id, AlumnoDTO alumno);
    Optional<Alumno> patch(Long id, JsonNode patch);
    Optional<Alumno> remove(Long id);
}
