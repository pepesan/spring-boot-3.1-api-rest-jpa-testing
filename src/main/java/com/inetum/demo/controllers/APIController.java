package com.inetum.demo.controllers;

import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import com.inetum.demo.dtos.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/dato")
@Tag(
        name = "dato",
        description = "the dato simple API"
)
public class APIController {
    public List<Dato> listado = new LinkedList<>();
    public Long lastID = 0L;
    @GetMapping("/")
    @Operation(
            summary = "show list of dato objects",
            description = "Shows a list of dato in an output array",
            tags = { "dato" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Dato.class)
                                    )
                            ),
                            @Content(
                                    mediaType = "application/xml",
                                    array = @ArraySchema(schema = @Schema(implementation = Dato.class))) })
    })
    public List<Dato> index(){
        return this.listado;
    }
    @GetMapping("/response")
    public ResponseEntity<List<Dato>> indexResponse(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.listado,
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
                                    schema = @Schema(implementation = Dato.class)
                                    ),
                            @Content(
                                    mediaType = "application/xml",
                                    schema = @Schema(implementation = Dato.class)
                            ),
                    })
    })
    public Dato addDato(
            @Parameter(description = "Created user object")
            @Valid @RequestBody DatoDTO dato) {
        lastID++;
        Dato d = new Dato();
        d.setId(lastID);
        d.setCadena(dato.getCadena());
        this.listado.add(d);
        return d;
    }
    @PostMapping("/response")
    public ResponseEntity<Dato> addDatoResponse(@Valid @RequestBody DatoDTO dato) {
        lastID++;
        Dato d = new Dato();
        d.setId(lastID);
        d.setCadena(dato.getCadena());
        this.listado.add(d);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                d,
                headers,
                HttpStatus.OK);
    }
    @GetMapping("/clear")
    List<Dato> clear(){
        this.listado = new LinkedList<>();
        this.lastID = 0L;
        return this.listado;
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
                                            implementation = Dato.class)
                            ),
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Dato.class)
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
    public ResponseEntity<Dato> showDatoById(@PathVariable("id") Long id){
        Dato d = this.listado
                .stream()
                .filter(dato -> dato.getId().equals(id))
                .findFirst()
                .orElse(null);

        HttpStatus status = HttpStatus.OK;
        if (d == null){
            status = HttpStatus.NOT_FOUND;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                d,
                headers,
                status);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Dato> editDatoById(
            @PathVariable("id") Long id,
            @RequestBody DatoDTO dato) {
        Dato d = this.listado.stream().filter(elemento ->
                elemento.getId().equals(id)).findFirst().orElse(null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.OK;
        if (d!=null){
            int index = this.listado.indexOf(d);
            d.setCadena(dato.getCadena());
            this.listado.set(index, d);
        }else{
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(
                d,
                headers,
                status
                );
    }

//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Dato> deleteDatoById(@PathVariable Long id){
//        Dato d = this.listado.stream().filter(elemento ->
//                elemento.getId().equals(id)).findFirst().orElse(null);
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpStatus status = HttpStatus.OK;
//        if (d!=null){
//            this.listado.remove(d);
//        }else{
//            status = HttpStatus.NOT_FOUND;
//        }
//        return new ResponseEntity<>(
//                d,
//                headers,
//                status
//        );
//    }
@DeleteMapping(value = "/{id}")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = {
                        @Content(
                                mediaType = "application/xml",
                                schema = @Schema(
                                        implementation = Dato.class)
                        ),
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(
                                        implementation = Dato.class)
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
public ResponseEntity<Dato> deleteDatoById(@PathVariable Long id){
    Dato d = this.listado.stream().filter(elemento ->
            elemento.getId().equals(id)).findFirst()
            .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                                "Not found with id = " + id
                        )
            );


    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpStatus status = HttpStatus.OK;

    this.listado.remove(d);

    return new ResponseEntity<>(
            d,
            headers,
            status
    );
}
}
