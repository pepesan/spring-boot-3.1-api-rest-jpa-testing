package com.inetum.demo.controllers.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.services.clientes.AlumnoClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador "cliente": no accede a la base de datos, sino que consulta con {@code WebClient}
 * el endpoint /api/v1/alumnos de esta misma aplicación (o del servidor en alumnos.api.base-url).
 */
@RestController
@RequestMapping("/api/v1/clientes/webclient/alumnos")
public class AlumnoWebClientClienteController {

    private final AlumnoClienteService service;

    public AlumnoWebClientClienteController(
            @Qualifier("alumnoWebClientClienteService") AlumnoClienteService service) {
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

    @DeleteMapping("/{id}")
    public Alumno delete(@PathVariable Long id) {
        return service.remove(id).orElseThrow(() ->
                new ResourceNotFoundException("Not found with id = " + id));
    }
}
