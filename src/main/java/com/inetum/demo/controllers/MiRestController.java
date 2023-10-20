package com.inetum.demo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "rest",
        description = "Rest API Sample"
)
public class MiRestController {
    @GetMapping("/")
    public String welcome() {//Welcome page, non-rest
        return "Hola Mundo";
    }
}
