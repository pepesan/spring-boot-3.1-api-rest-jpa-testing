package com.inetum.demo.controllers.herencia;

import com.inetum.demo.domain.herencia.Empleado;
import com.inetum.demo.services.herencia.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final PersonaService personaService;

    @Autowired
    public EmpleadoController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/")
    public List<Empleado> listar() {
        return this.personaService.getAllEmpleados();
    }

    @PostMapping("/")
    public Empleado add(@RequestBody Empleado empleado) {
        return this.personaService.saveEmpleado(empleado);
    }

    // Endpoint para obtener empleados con sueldo mayor a un valor dado (por defecto 20,000)
    @GetMapping("/sueldo-mayor/{sueldo}")
    public List<Empleado> getEmpleadosConSueldoMayorA(@PathVariable("sueldo") double sueldo) {
        return personaService.getEmpleadosConSueldoMayorA(sueldo);
    }
}
