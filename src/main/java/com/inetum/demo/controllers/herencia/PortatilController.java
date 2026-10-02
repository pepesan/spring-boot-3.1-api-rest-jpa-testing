package com.inetum.demo.controllers.herencia;

import com.inetum.demo.domain.herencia.Portatil;
import com.inetum.demo.dtos.PortatilDTO;
import com.inetum.demo.services.herencia.PortatilService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** CRUD de Portatil: el cuerpo de POST/PUT es un PortatilDTO ({"marca","modelo","peso","duracionBateria"}). */
@RestController
@RequestMapping("/api/v1/portatiles")
public class PortatilController {

    private final PortatilService portatilService;

    @Autowired
    public PortatilController(PortatilService portatilService) {
        this.portatilService = portatilService;
    }

    @GetMapping("/")
    @Operation(summary = "list laptops", tags = {"portatil"})
    public ResponseEntity<List<Portatil>> index() {
        return ResponseEntity.ok(this.portatilService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a laptop", tags = {"portatil"})
    public ResponseEntity<Portatil> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.portatilService.findById(id).orElseThrow(() -> notFound(id)));
    }

    @PostMapping("/")
    @Operation(summary = "create a laptop", tags = {"portatil"})
    public ResponseEntity<Portatil> create(@Valid @RequestBody PortatilDTO dto) {
        Portatil portatil = this.portatilService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(portatil.getId()).toUri();
        return ResponseEntity.created(location).body(portatil);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a laptop", tags = {"portatil"})
    public ResponseEntity<Portatil> replace(@PathVariable Long id, @Valid @RequestBody PortatilDTO dto) {
        return ResponseEntity.ok(this.portatilService.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a laptop", tags = {"portatil"})
    public ResponseEntity<Portatil> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.portatilService.delete(id).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
