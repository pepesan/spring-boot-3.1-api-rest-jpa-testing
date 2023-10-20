package com.inetum.demo.controllers;

import com.inetum.demo.dtos.Dato;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "dato",
        description = "the dato simple API"
)
public class DatoController {
    @GetMapping(value = "/dato-simple")
    public Dato index(){
        return new Dato(1L,"Hola");
    }
    @GetMapping(value = "/dato-response")
    public ResponseEntity<Dato> indexResponse(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(new Dato(1L,"Hola"), headers, HttpStatus.OK);
    }
    @GetMapping("/dato-json")
    public ResponseEntity<String> indexJson(){
        Dato d = new Dato(1L,"Hola");
        d.setId(1L);
        System.out.println(d.getId());
        d.setCadena("Hola");
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\"=");
        sb.append(d.getId()+", ");
        sb.append("\"cadena\"=\"");
        sb.append(d.getCadena());
        sb.append("\"");
        sb.append("}");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(sb.toString(), headers, HttpStatus.OK);
    }
}
