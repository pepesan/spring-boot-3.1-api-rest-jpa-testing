package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Ordenador;
import com.inetum.demo.services.herencia.joined.OrdenadorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vista polimórfica de la herencia JOINED: lista, consulta y borra cualquier ordenador.
 * Para crear/modificar hay un controlador por tipo (portátiles, sobremesas).
 */
@RestController
@RequestMapping("/api/v1/ordenadores")
public class OrdenadorController {

    private final OrdenadorService service;

    @Autowired
    public OrdenadorController(OrdenadorService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list every ordenador (portátiles, sobremesas)", tags = {"ordenador"})
    public ResponseEntity<List<Ordenador>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a ordenador of any type", tags = {"ordenador"})
    public ResponseEntity<Ordenador> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a ordenador of any type", tags = {"ordenador"})
    public ResponseEntity<Ordenador> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.delete(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }
}
