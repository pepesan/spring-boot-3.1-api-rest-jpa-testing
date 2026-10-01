package com.inetum.demo.controllers.gateway;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.services.gateway.AlumnoGatewayService;
import jakarta.validation.Valid;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que consulta el servicio de alumnos a través de un gateway con reintentos.
 * Si el remoto falla de forma persistente responde 503 (ver ControllerExceptionHandler);
 * en el listado, si hay una lista anterior, la devuelve con la cabecera X-Datos-Obsoletos.
 */
@RestController
@RequestMapping("/api/v1/gateway/alumnos")
public class AlumnoGatewayController {

    public static final String HEADER_OBSOLETO = "X-Datos-Obsoletos";

    private final AlumnoGatewayService service;

    public AlumnoGatewayController(AlumnoGatewayService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Alumno>> index() {
        AlumnoGatewayService.Listado listado = service.findAll();
        ResponseEntity.BodyBuilder respuesta = ResponseEntity.ok();
        if (listado.obsoleto()) {
            respuesta.header(HEADER_OBSOLETO, "true");
        }
        return respuesta.body(listado.alumnos());
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
