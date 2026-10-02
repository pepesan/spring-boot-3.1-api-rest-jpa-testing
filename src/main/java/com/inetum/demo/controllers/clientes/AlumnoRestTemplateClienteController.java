package com.inetum.demo.controllers.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.services.clientes.AlumnoClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * Controlador "cliente": no accede a la base de datos, sino que consulta con {@code RestTemplate}
 * el endpoint /api/v1/alumnos de esta misma aplicación (o del servidor en alumnos.api.base-url).
 */
@RestController
@RequestMapping("/api/v1/clientes/resttemplate/alumnos")
public class AlumnoRestTemplateClienteController {

    private final AlumnoClienteService service;

    public AlumnoRestTemplateClienteController(
            @Qualifier("alumnoRestTemplateClienteService") AlumnoClienteService service) {
        this.service = service;
    }

    @GetMapping("/")
    public List<Alumno> index() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Alumno show(@PathVariable Long id) {
        return service.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found with id = " + id));
    }

    @PostMapping("/")
    public Alumno add(@Valid @RequestBody AlumnoDTO alumno) {
        return service.create(alumno);
    }

    @PutMapping("/{id}")
    public Alumno replace(@PathVariable Long id, @Valid @RequestBody AlumnoDTO alumno) {
        return service.update(id, alumno).orElseThrow(() ->
                new ResourceNotFoundException("Not found with id = " + id));
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public Alumno patch(@PathVariable Long id, @RequestBody JsonNode patch) {
        return service.patch(id, patch).orElseThrow(() ->
                new ResourceNotFoundException("Not found with id = " + id));
    }

    @DeleteMapping("/{id}")
    public Alumno delete(@PathVariable Long id) {
        return service.remove(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found with id = " + id));
    }
}
