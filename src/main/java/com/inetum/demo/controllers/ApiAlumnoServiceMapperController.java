package com.inetum.demo.controllers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.services.AlumnoServiceMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alumnomapper")
public class ApiAlumnoServiceMapperController {

    public AlumnoServiceMapper alumnoService;


    @Autowired
    ApiAlumnoServiceMapperController (AlumnoServiceMapper alumnoService){
        this.alumnoService = alumnoService;
    }

    @GetMapping
    public ResponseEntity<List<Alumno>> findAll(){
        return ResponseEntity.ok(this.alumnoService.findAll());
    }

    @PostMapping
    public ResponseEntity<Alumno> addItem(@Valid @RequestBody AlumnoDTO alumnoDTO){
        Alumno alumno = this.alumnoService.save(alumnoDTO);
        return ResponseEntity.ok(alumno);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> findById(@PathVariable ("id") Long id){
        Alumno alumno = this.alumnoService.findById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;
        return new ResponseEntity<>(
                alumno,
                headers,
                status
        );
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Alumno> editDatoById(
            @PathVariable("id") Long id,
            @Valid @RequestBody AlumnoDTO dato) {
        Alumno alumno = this.alumnoService.update(dato, id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;

        return new ResponseEntity<>(
                alumno,
                headers,
                status
        );
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Alumno> deleteDatoById(@PathVariable Long id){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;
        Alumno alumno = this.alumnoService.remove(id);

        return new ResponseEntity<>(
                alumno,
                headers,
                status
        );
    }


}
