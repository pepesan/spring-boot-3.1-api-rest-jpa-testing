package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Empleado;
import com.inetum.demo.dtos.herencia.joined.EmpleadoDTO;
import com.inetum.demo.services.herencia.joined.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/** Lectura, alta y modificación de Empleado (subclase de Persona); el borrado se hace en /api/v1/personas. */
@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    @Autowired
    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list empleados only", tags = {"persona"})
    public ResponseEntity<List<Empleado>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a Empleado (404 if the id belongs to another type)", tags = {"persona"})
    public ResponseEntity<Empleado> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id).orElseThrow(() -> notFound(id)));
    }

    @GetMapping("/sueldo-mayor/{sueldo}")
    @Operation(summary = "list empleados (jefes included) earning more than the given salary", tags = {"persona"})
    public ResponseEntity<List<Empleado>> sueldoMayor(@PathVariable double sueldo) {
        return ResponseEntity.ok(this.service.findBySueldoMayorA(sueldo));
    }

    @PostMapping("/")
    @Operation(summary = "create a Empleado", tags = {"persona"})
    public ResponseEntity<Empleado> create(@Valid @RequestBody EmpleadoDTO dto) {
        Empleado creado = this.service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "replace a Empleado", tags = {"persona"})
    public ResponseEntity<Empleado> replace(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO dto) {
        return ResponseEntity.ok(this.service.update(id, dto).orElseThrow(() -> notFound(id)));
    }

    private static ResourceNotFoundException notFound(Long id) {
        return new ResourceNotFoundException("Not found with id = " + id);
    }
}
