package com.inetum.demo.controllers.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Coche;
import com.inetum.demo.dtos.herencia.singletable.CocheDTO;
import com.inetum.demo.services.herencia.singletable.CocheService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Coche (subclase de Vehiculo); el borrado se hace en /api/v1/vehiculos. */
@RestController
@RequestMapping("/api/v1/coches")
public class CocheController {

    private final CocheService service;

    @Autowired
    public CocheController(CocheService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list coches only", tags = {"vehiculo"})
    public ResponseEntity<List<Coche>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Coche (404 if the id belongs to another type)", tags = {"vehiculo"})
    public ResponseEntity<Coche> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a Coche", tags = {"vehiculo"})
    public ResponseEntity<Coche> create(@Valid @RequestBody CocheDTO dto) {
        Coche creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Coche", tags = {"vehiculo"})
    public ResponseEntity<Coche> replace(@PathVariable Long id, @Valid @RequestBody CocheDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
