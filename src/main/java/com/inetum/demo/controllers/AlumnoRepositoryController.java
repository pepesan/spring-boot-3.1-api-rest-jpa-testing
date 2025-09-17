package com.inetum.demo.controllers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.dtos.PageResponse;
import com.inetum.demo.repositories.AlumnoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;

@RestController
@RequestMapping("/api/v1/repository")
public class AlumnoRepositoryController {

    // @Autowired
    private final AlumnoRepository alumnoRepository;

    @Autowired
    public AlumnoRepositoryController(AlumnoRepository alumnoRepository){
        this.alumnoRepository = alumnoRepository;
    }

    @GetMapping("/")
    public ResponseEntity<List<Alumno>> index(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.alumnoRepository.findAll(),
                headers,
                HttpStatus.OK);

    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<Alumno>> search(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        int safePage = Math.max(page, 1) - 1;                 // 1-based -> 0-based
        int safeSize = Math.min(Math.max(size, 1), 100);      // cap 100
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("id").ascending());

        Page<Alumno> pageResult = alumnoRepository.findAll(pageable);
        PageResponse<Alumno> body = PageResponse.from(pageResult);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @PostMapping("/")
    public ResponseEntity<Alumno> create(
            @Valid @RequestBody AlumnoDTO alumnoDto){
        Alumno alumno = new Alumno(alumnoDto);
        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> show(@PathVariable("id") Long id){
        Alumno  alumno = alumnoRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Not found with id = " + id
                ));
        return ResponseEntity.ok(alumno);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody AlumnoDTO alumnoDto) {
        Alumno alumno = alumnoRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Not found with id = " + id
                ));
        alumno.setNombre(alumnoDto.getNombre());
        alumno.setApellidos(alumnoDto.getApellidos());
        alumno.setEdad(alumnoDto.getEdad());
        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Alumno> delete(@PathVariable("id") Long id){
        Alumno alumno = alumnoRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Not found with id = " + id
                ));
        alumnoRepository.delete(alumno);
        return ResponseEntity.ok(alumno);  //200 OK
    }

}
