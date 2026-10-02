package com.inetum.demo.controllers.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Camion;
import com.inetum.demo.dtos.herencia.singletable.CamionDTO;
import com.inetum.demo.services.herencia.singletable.CamionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Camion (subclase de Vehiculo); el borrado se hace en /api/v1/vehiculos. */
@RestController
@RequestMapping("/api/v1/camiones")
public class CamionController {

    private final CamionService service;

    @Autowired
    public CamionController(CamionService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list camiones only", tags = {"vehiculo"})
    public ResponseEntity<List<Camion>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Camion (404 if the id belongs to another type)", tags = {"vehiculo"})
    public ResponseEntity<Camion> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Camion", tags = {"vehiculo"})
    public ResponseEntity<Camion> create(@Valid @RequestBody CamionDTO dto) {
        Camion creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Camion", tags = {"vehiculo"})
    public ResponseEntity<Camion> replace(@PathVariable Long id, @Valid @RequestBody CamionDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
