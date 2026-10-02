package com.inetum.demo.controllers.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Moto;
import com.inetum.demo.dtos.herencia.singletable.MotoDTO;
import com.inetum.demo.services.herencia.singletable.MotoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Moto (subclase de Vehiculo); el borrado se hace en /api/v1/vehiculos. */
@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

    private final MotoService service;

    @Autowired
    public MotoController(MotoService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list motos only", tags = {"vehiculo"})
    public ResponseEntity<List<Moto>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Moto (404 if the id belongs to another type)", tags = {"vehiculo"})
    public ResponseEntity<Moto> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Moto", tags = {"vehiculo"})
    public ResponseEntity<Moto> create(@Valid @RequestBody MotoDTO dto) {
        Moto creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Moto", tags = {"vehiculo"})
    public ResponseEntity<Moto> replace(@PathVariable Long id, @Valid @RequestBody MotoDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
