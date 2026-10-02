package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Sobremesa;
import com.inetum.demo.dtos.herencia.joined.SobremesaDTO;
import com.inetum.demo.services.herencia.joined.SobremesaService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Sobremesa (subclase de Ordenador); el borrado se hace en /api/v1/ordenadores. */
@RestController
@RequestMapping("/api/v1/sobremesas")
public class SobremesaController {

    private final SobremesaService service;

    @Autowired
    public SobremesaController(SobremesaService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list sobremesas only", tags = {"ordenador"})
    public ResponseEntity<List<Sobremesa>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Sobremesa (404 if the id belongs to another type)", tags = {"ordenador"})
    public ResponseEntity<Sobremesa> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Sobremesa", tags = {"ordenador"})
    public ResponseEntity<Sobremesa> create(@Valid @RequestBody SobremesaDTO dto) {
        Sobremesa creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Sobremesa", tags = {"ordenador"})
    public ResponseEntity<Sobremesa> replace(@PathVariable Long id, @Valid @RequestBody SobremesaDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
