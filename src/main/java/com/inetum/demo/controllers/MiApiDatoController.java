package com.inetum.demo.controllers;

import com.inetum.demo.dtos.Dato;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
@RestController
@RequestMapping(value = "/api/dato")
public class MiApiDatoController {
    public List<Dato> listado = new LinkedList<>();
    public Long lastID = 0L;

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
    public Dato addDato(@RequestBody Dato dato) {
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
            @RequestBody Dato dato) {
        Dato d = this.listado.stream().filter(elemento ->
                elemento.getId().equals(id)).findFirst().orElse(null);
        if (dato!=null){
            int index = this.listado.indexOf(d);
            dato.setId(id);
            this.listado.set(index, dato);
            return dato;
        }else{
            return new Dato();
        }
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
