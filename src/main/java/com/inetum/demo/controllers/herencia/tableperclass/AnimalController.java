package com.inetum.demo.controllers.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Animal;
import com.inetum.demo.services.herencia.tableperclass.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vista polimórfica de la herencia TABLE_PER_CLASS: lista, consulta y borra cualquier animal.
 * Para crear/modificar hay un controlador por tipo (perros, gatos).
 */
@RestController
@RequestMapping("/api/v1/animales")
public class AnimalController {

    private final AnimalService service;

    @Autowired
    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @GetMapping("/")
    @Operation(summary = "list every animal (perros, gatos)", tags = {"animal"})
    public ResponseEntity<List<Animal>> index() {
        return ResponseEntity.ok(this.service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a animal of any type", tags = {"animal"})
    public ResponseEntity<Animal> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a animal of any type", tags = {"animal"})
    public ResponseEntity<Animal> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.service.delete(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }
}
