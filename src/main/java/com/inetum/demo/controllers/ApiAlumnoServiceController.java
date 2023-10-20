package com.inetum.demo.controllers;

import com.inetum.demo.services.AlumnoService;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.dtos.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/alumnos")
public class ApiAlumnoServiceController {
    AlumnoService alumnoService;

    @Autowired
    ApiAlumnoServiceController(AlumnoService alumnoService){
        this.alumnoService = alumnoService;
    }

    @GetMapping("/")
    @Operation(
            summary = "show list of alumno objects",
            description = "Shows a list of alumno in an output array",
            tags = { "alumno" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Alumno.class)
                                    )
                            ),
                            @Content(
                                    mediaType = "application/xml",
                                    array = @ArraySchema(schema = @Schema(implementation = Alumno.class))
                            )
                    })
    })
    public ResponseEntity<List<Alumno>> index(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.alumnoService.findAll(),
                headers,
                HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(
            summary = "add a dato object",
            description = "add a dato object with a string input of 80 max characters",
            tags = { "dato" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dato added successfully",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation =
                                            Alumno.class)
                            ),
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(implementation = Alumno.class)
                            ),
                    })
    })
    public ResponseEntity<Alumno> addDato(
            @Parameter(description = "Created user object")
            @Valid @RequestBody AlumnoDTO dato) {
        Alumno alumno = new Alumno(dato);
        this.alumnoService.save(alumno);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                alumno,
                headers,
                HttpStatus.OK);
    }
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(
                                            implementation = Alumno.class)
                            ),
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Alumno.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Invalid input",
                    content = {
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class)
                            ),
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class)
                            )
                    })
    })
    public ResponseEntity<Alumno> showDatoById(@PathVariable("id") Long id){
        Optional<Alumno> alumnoOpcional = this.alumnoService.findById(id);
        alumnoOpcional.orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;
        Alumno alumno = alumnoOpcional.get();

        return new ResponseEntity<>(
                alumno,
                headers,
                status
        );
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<Alumno> editDatoById(
            @PathVariable("id") Long id,
            @RequestBody AlumnoDTO dato) {
        Optional<Alumno> alumnoOpcional = this.alumnoService.findById(id);
        alumnoOpcional.orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        Alumno alumno = new Alumno(dato);
        alumno.setId(id);
        this.alumnoService.save(alumno);
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(
                                            implementation = Alumno.class)
                            ),
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Alumno.class)
                            )
                    }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Object not found",
                    content = {
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class)
                            ),
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorMessage.class)
                            )
                    })
    })
    public ResponseEntity<Alumno> deleteDatoById(@PathVariable Long id){
        Optional<Alumno> alumnoOpcional = this.alumnoService.findById(id);
        alumnoOpcional.orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;
        Alumno alumno = alumnoOpcional.get();
        this.alumnoService.remove(alumno);

        return new ResponseEntity<>(
                alumno,
                headers,
                status
        );
    }
}
