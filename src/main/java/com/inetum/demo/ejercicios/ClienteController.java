package com.inetum.demo.ejercicios;

import com.inetum.demo.domain.Alumno;
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
@RequestMapping("/api/v1/cliente")
public class ClienteController {
    private  ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService){
        this.clienteService=clienteService;
    }
    @GetMapping("/")
    public ResponseEntity<List<Cliente>> index(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.clienteService.findAll(),
                headers,
                HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Cliente> add(@Valid @RequestBody ClienteDTO clienteDTO){
        Cliente cliente = new Cliente(clienteDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.clienteService.save(cliente),
                headers,
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable("id") Long id){
        Cliente cliente = this.clienteService.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                cliente,
                headers,
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateById(
            @PathVariable("id") Long id,
            @Valid @RequestBody ClienteDTO clienteDTO
    ){
        Cliente cliente = this.clienteService.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        cliente.setName(clienteDTO.getName());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setDir(clienteDTO.getDir());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                this.clienteService.save(cliente),
                headers,
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Cliente> deleteById(@PathVariable("id") Long id){
        Cliente cliente = this.clienteService.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        this.clienteService.delete(cliente);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(
                cliente,
                headers,
                HttpStatus.OK);
    }
}
