package com.inetum.demo.services.gateway;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

/** Casos de uso sobre alumnos obtenidos a través del gateway resiliente. */
public interface AlumnoGatewayService {

    /**
     * @param alumnos listado
     * @param obsoleto true si el servicio remoto falló y se devuelve la última lista conocida
     */
    record Listado(List<Alumno> alumnos, boolean obsoleto) {
        public Listado {
            alumnos = List.copyOf(alumnos);   // copia inmutable: el listado no se puede alterar desde fuera
        }
    }

    Listado findAll();

    Optional<Alumno> findById(Long id);

    Alumno create(AlumnoDTO alumno);

    Optional<Alumno> update(Long id, AlumnoDTO alumno);
    Optional<Alumno> patch(Long id, JsonNode patch);
    Optional<Alumno> remove(Long id);
}
