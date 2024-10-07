package com.inetum.demo.controllers.herencia;

import com.inetum.demo.domain.herencia.Empleado;
import com.inetum.demo.domain.herencia.Jefe;
import com.inetum.demo.services.herencia.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jefes")
public class JefeController {

    private final PersonaService personaService;

    @Autowired
    public JefeController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/")
    public List<Jefe> listar() {
        return this.personaService.getAllJefes();
    }

    @PostMapping("/")
    public Empleado add(@RequestBody Jefe jefe) {
        return this.personaService.saveJefe(jefe);
    }
}
