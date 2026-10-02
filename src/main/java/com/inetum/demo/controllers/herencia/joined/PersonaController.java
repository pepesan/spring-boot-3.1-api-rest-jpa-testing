package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Persona;
import com.inetum.demo.services.herencia.joined.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vista polimórfica de la herencia JOINED (Persona -> Empleado -> Jefe): lista, consulta y borra cualquier persona.
 * Para crear/modificar hay un controlador por tipo (empleados, jefes).
 */
@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {

    private final PersonaService service;

    @Autowired
    public PersonaController(PersonaService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list every persona (empleados, jefes)", tags = {"persona"})
    public ResponseEntity<List<Persona>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a persona of any type", tags = {"persona"})
    public ResponseEntity<Persona> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a persona of any type", tags = {"persona"})
    public ResponseEntity<Persona> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.delete(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }
}
