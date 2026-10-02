package com.inetum.demo.controllers.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Perro;
import com.inetum.demo.dtos.herencia.tableperclass.PerroDTO;
import com.inetum.demo.services.herencia.tableperclass.PerroService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Perro (subclase de Animal); el borrado se hace en /api/v1/animales. */
@RestController
@RequestMapping("/api/v1/perros")
public class PerroController {

    private final PerroService service;

    @Autowired
    public PerroController(PerroService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list perros only", tags = {"animal"})
    public ResponseEntity<List<Perro>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Perro (404 if the id belongs to another type)", tags = {"animal"})
    public ResponseEntity<Perro> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Perro", tags = {"animal"})
    public ResponseEntity<Perro> create(@Valid @RequestBody PerroDTO dto) {
        Perro creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Perro", tags = {"animal"})
    public ResponseEntity<Perro> replace(@PathVariable Long id, @Valid @RequestBody PerroDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
