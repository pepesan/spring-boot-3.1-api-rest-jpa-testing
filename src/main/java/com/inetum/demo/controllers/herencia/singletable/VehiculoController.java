package com.inetum.demo.controllers.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Vehiculo;
import com.inetum.demo.services.herencia.singletable.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Vista polimórfica de la herencia SINGLE_TABLE: lista, consulta y borra cualquier vehículo.
 * Para crear/modificar hay un controlador por tipo (coches, motos, camiones).
 */
@RestController
@RequestMapping("/api/v1/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/")
    @Operation(summary = "list every vehicle (cars, motorbikes, trucks)", tags = {"vehiculo"})
    public ResponseEntity<List<Vehiculo>> index() {
        return ResponseEntity.ok(this.vehiculoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "show a vehicle of any type", tags = {"vehiculo"})
    public ResponseEntity<Vehiculo> show(@PathVariable Long id) {
        return ResponseEntity.ok(this.vehiculoService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete a vehicle of any type", tags = {"vehiculo"})
    public ResponseEntity<Vehiculo> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.vehiculoService.delete(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id)));
    }
}
