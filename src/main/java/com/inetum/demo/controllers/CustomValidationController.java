package com.inetum.demo.controllers;


import com.inetum.demo.dtos.ValidadoDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/custom")
public class CustomValidationController {

    @PostMapping
    public ValidadoDTO index(@Valid @RequestBody ValidadoDTO validadoDTO){
        return validadoDTO;
    }
}
