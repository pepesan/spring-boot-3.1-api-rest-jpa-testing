package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Portatil;
import com.inetum.demo.dtos.herencia.joined.PortatilDTO;
import com.inetum.demo.services.herencia.joined.PortatilService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Portatil (subclase de Ordenador); el borrado se hace en /api/v1/ordenadores. */
@RestController
@RequestMapping("/api/v1/portatiles")
public class PortatilController {

    private final PortatilService service;

    @Autowired
    public PortatilController(PortatilService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list portatiles only", tags = {"ordenador"})
    public ResponseEntity<List<Portatil>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Portatil (404 if the id belongs to another type)", tags = {"ordenador"})
    public ResponseEntity<Portatil> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Portatil", tags = {"ordenador"})
    public ResponseEntity<Portatil> create(@Valid @RequestBody PortatilDTO dto) {
        Portatil creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Portatil", tags = {"ordenador"})
    public ResponseEntity<Portatil> replace(@PathVariable Long id, @Valid @RequestBody PortatilDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
