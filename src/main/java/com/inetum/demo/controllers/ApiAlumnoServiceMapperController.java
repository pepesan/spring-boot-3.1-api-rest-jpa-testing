package com.inetum.demo.controllers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.patch.MergePatchOperation;
import com.inetum.demo.patch.MergePatchService;
import tools.jackson.databind.JsonNode;
import com.inetum.demo.services.AlumnoServiceMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final MergePatchService mergePatch;


    @Autowired
    ApiAlumnoServiceMapperController (@Qualifier("alumnoServiceMapperImpl") AlumnoServiceMapper alumnoService,
                                      MergePatchService mergePatch){
        this.alumnoService = alumnoService;
        this.mergePatch = mergePatch;
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

    @MergePatchOperation
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Alumno> patchDatoById(
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/merge-patch+json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"nombre\": \"Marta2\"}")))
            @RequestBody JsonNode patch) {
        // findById lanza ResourceNotFoundException (404) si no existe
        AlumnoDTO parcheado = mergePatch.apply(AlumnoDTO.from(this.alumnoService.findById(id)), patch);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(this.alumnoService.update(parcheado, id), headers, HttpStatus.OK);
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
