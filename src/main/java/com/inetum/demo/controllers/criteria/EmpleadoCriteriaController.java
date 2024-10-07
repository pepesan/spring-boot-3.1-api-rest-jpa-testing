package com.inetum.demo.controllers.criteria;

import com.inetum.demo.domain.herencia.Empleado;
import com.inetum.demo.services.criteria.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empleados-criteria")
public class EmpleadoCriteriaController {

    public final EmpleadoService empleadoService;

    @Autowired
    public EmpleadoCriteriaController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/nombre/{nombre}/edad/{edad}")
    public List<Empleado> buscarEmpleados(
            @PathVariable(required = false) String nombre,
            @PathVariable(required = false) Integer edad) {
        return empleadoService.buscarEmpleadosConCriteria(nombre, edad);
    }
}
