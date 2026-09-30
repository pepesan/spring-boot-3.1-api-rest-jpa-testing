package com.inetum.demo.controllers;

import com.inetum.demo.dtos.Dato;
import com.inetum.demo.patch.MergePatchOperation;
import com.inetum.demo.patch.MergePatchService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import java.util.LinkedList;
import java.util.List;
@RestController
@RequestMapping(value = "/api/dato")
public class MiApiDatoController {
    public List<Dato> listado = new LinkedList<>();
    public Long lastID = 0L;

    private final MergePatchService mergePatch;

    public MiApiDatoController(MergePatchService mergePatch) {
        this.mergePatch = mergePatch;
    }

    @GetMapping("/clear")
    List<Dato> clear(){
        this.listado = new LinkedList<>();
        this.lastID = 0L;
        return this.listado;
    }

    @GetMapping
    public List<Dato> index(){
        return this.listado;
    }
    @PostMapping
    public Dato addDato(@Valid @RequestBody Dato dato) {
        lastID++;
        dato.setId(lastID);
        this.listado.add(dato);
        return dato;
    }
    @GetMapping("/{id}")
    public Dato showDatoById(@PathVariable("id") Long id){
        Dato d = this.listado.stream().filter(dato ->
                dato.getId().equals(id)).findFirst().orElse(null);
        // System.out.println(d);
        return d;
    }
    @PutMapping(value = "/{id}")
    public Dato editDatoById(
            @PathVariable("id") Long id,
            @Valid
            @RequestBody Dato dato) {
        Dato d = this.listado.stream().filter(elemento ->
                elemento.getId().equals(id)).findFirst().orElse(null);
        int index = this.listado.indexOf(d);
        if (d!=null && index!=-1){
            dato.setId(id);
            this.listado.set(index, dato);
            return dato;
        }else{
            return new Dato();
        }
    }
    /**
     * Modificación parcial según JSON Merge Patch (RFC 7386): el cliente envía solo los
     * campos a cambiar, con Content-Type application/merge-patch+json.
     * El id nunca se modifica y el resultado se valida antes de guardarse.
     */
    @MergePatchOperation
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public Dato patchDatoById(
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "application/merge-patch+json",
                            examples = @ExampleObject(value = "{\"cadena\": \"valor1\"}")))
            @RequestBody JsonNode patch) {
        Dato actual = this.listado.stream().filter(elemento ->
                elemento.getId().equals(id)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Not found with id = " + id));
        // Se aplica el parche sobre una copia para no tocar el original si no es válido
        Dato parcheado = mergePatch.apply(new Dato(actual.getId(), actual.getCadena()), patch);
        parcheado.setId(id);
        this.listado.set(this.listado.indexOf(actual), parcheado);
        return parcheado;
    }
    @DeleteMapping(value = "/{id}")
    public Dato deleteDatoById(@PathVariable Long id){
        Dato d = this.listado.stream().filter(elemento ->
                elemento.getId().equals(id)).findFirst().orElse(null);
        if (d !=null){
            this.listado.remove(d);
            return d;
        }else{
            return new Dato();
        }
    }
}
