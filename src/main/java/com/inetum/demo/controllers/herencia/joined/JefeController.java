package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Jefe;
import com.inetum.demo.dtos.herencia.joined.JefeDTO;
import com.inetum.demo.services.herencia.joined.JefeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Jefe (subclase de Persona); el borrado se hace en /api/v1/personas. */
@RestController
@RequestMapping("/api/v1/jefes")
public class JefeController {

    private final JefeService service;

    @Autowired
    public JefeController(JefeService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list jefes only", tags = {"persona"})
    public ResponseEntity<List<Jefe>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Jefe (404 if the id belongs to another type)", tags = {"persona"})
    public ResponseEntity<Jefe> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Jefe", tags = {"persona"})
    public ResponseEntity<Jefe> create(@Valid @RequestBody JefeDTO dto) {
        Jefe creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Jefe", tags = {"persona"})
    public ResponseEntity<Jefe> replace(@PathVariable Long id, @Valid @RequestBody JefeDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
