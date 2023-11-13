package com.inetum.demo.controllers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import com.inetum.demo.repositories.AlumnoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repository")
public class AlumnoRepositoryController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping("/")
    public ResponseEntity<List<Alumno>> index(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.alumnoRepository.findAll(),
                headers,
                HttpStatus.OK);

    }

    @PostMapping("/")
    public ResponseEntity<Alumno> create(
            @Valid @RequestBody AlumnoDTO alumnoDto){
        Alumno alumno = new Alumno(alumnoDto);
        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> show(@PathVariable Long id){
        return ResponseEntity.ok(alumnoRepository.findById(id).get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> update(
            @PathVariable Long id,
            @Valid @RequestBody AlumnoDTO alumnoDto) {
        Alumno alumno = alumnoRepository.findById(id).get();
        alumno.setNombre(alumnoDto.getNombre());
        alumno.setApellidos(alumnoDto.getApellidos());
        return ResponseEntity.ok(alumnoRepository.save(alumno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Alumno> delete(@PathVariable Long id){
        Alumno alumno = alumnoRepository.findById(id).get();
        alumnoRepository.delete(alumno);
        return ResponseEntity.ok(alumno);  //200 OK
    }

}
