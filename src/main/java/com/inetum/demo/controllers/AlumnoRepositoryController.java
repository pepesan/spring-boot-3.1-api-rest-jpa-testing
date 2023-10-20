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

}
