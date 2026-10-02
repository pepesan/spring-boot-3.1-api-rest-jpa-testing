package com.inetum.demo.controllers.gateway;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Endpoint de apoyo (demo/tests) que imita /api/v1/alumnos pero con fallos programables, para
 * probar los reintentos del gateway: apuntar {@code alumnos.gateway.path} a
 * {@code /api/v1/inestable/alumnos}.
 * <ul>
 *   <li>{@code POST /config?fallos=N}: las N primeras llamadas de datos responden 500</li>
 *   <li>{@code POST /config?lentoMs=M}: cada llamada de datos tarda M ms (para provocar timeouts)</li>
 *   <li>{@code GET /llamadas}: nº de llamadas de datos recibidas; {@code POST /reset}: lo deja todo a cero</li>
 *   <li>PUT y PATCH existen también y devuelven el alumno modificado</li>
 *   <li>el id 404 responde siempre 404 (para comprobar que un 4xx no se reintenta)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/inestable/alumnos")
public class AlumnoInestableController {

    private final AtomicInteger llamadas = new AtomicInteger();
    private final AtomicInteger fallos = new AtomicInteger();
    private final AtomicLong lentoMs = new AtomicLong();

    @PostMapping("/config")
    public Map<String, Object> config(@RequestParam(defaultValue = "0") int fallos,
                                      @RequestParam(defaultValue = "0") long lentoMs) {
        this.llamadas.set(0);
        this.fallos.set(fallos);
        this.lentoMs.set(lentoMs);
        return estado();
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        return config(0, 0);
    }

    @GetMapping("/llamadas")
    public Map<String, Object> estado() {
        return Map.of("llamadas", llamadas.get(), "fallos", fallos.get(), "lentoMs", lentoMs.get());
    }

    @GetMapping("/")
    public List<Alumno> index() {
        simular();
        return List.of(alumno(1L));
    }

    @GetMapping("/{id}")
    public Alumno show(@PathVariable Long id) {
        simular();
        if (id == 404L) {
            throw new ResourceNotFoundException("Not found with id = " + id);
        }
        return alumno(id);
    }

    @PostMapping("/")
    public Alumno add(@RequestBody AlumnoDTO dto) {
        simular();
        return new Alumno(1L, dto.getNombre(), dto.getApellidos(), dto.getEdad());
    }

    @PutMapping("/{id}")
    public Alumno replace(@PathVariable Long id, @RequestBody AlumnoDTO dto) {
        simular();
        if (id == 404L) {
            throw new ResourceNotFoundException("Not found with id = " + id);
        }
        return new Alumno(id, dto.getNombre(), dto.getApellidos(), dto.getEdad());
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public Alumno patch(@PathVariable Long id, @RequestBody JsonNode patch) {
        simular();
        if (id == 404L) {
            throw new ResourceNotFoundException("Not found with id = " + id);
        }
        Alumno alumno = alumno(id);
        if (patch.has("nombre")) {
            alumno.setNombre(patch.get("nombre").asString());
        }
        return alumno;
    }

    @DeleteMapping("/{id}")
    public Alumno delete(@PathVariable Long id) {
        simular();
        if (id == 404L) {
            throw new ResourceNotFoundException("Not found with id = " + id);
        }
        return alumno(id);
    }

    private void simular() {
        int numero = llamadas.incrementAndGet();
        long espera = lentoMs.get();
        if (espera > 0) {
            try {
                Thread.sleep(espera);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (numero <= fallos.get()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "fallo simulado " + numero);
        }
    }

    private static Alumno alumno(Long id) {
        return new Alumno(id, "Inestable", "Demo", 30);
    }
}
