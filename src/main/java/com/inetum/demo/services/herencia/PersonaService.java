package com.inetum.demo.services.herencia;

import com.inetum.demo.domain.herencia.Empleado;
import com.inetum.demo.domain.herencia.Jefe;
import com.inetum.demo.domain.herencia.Persona;
import com.inetum.demo.repositories.herencia.EmpleadoRepository;
import com.inetum.demo.repositories.herencia.JefeRepository;
import com.inetum.demo.repositories.herencia.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final JefeRepository jefeRepository;

    @Autowired
    public PersonaService(PersonaRepository personaRepository, EmpleadoRepository empleadoRepository, JefeRepository jefeRepository) {
        this.personaRepository = personaRepository;
        this.empleadoRepository = empleadoRepository;
        this.jefeRepository = jefeRepository;
    }

    // Métodos para gestionar las personas
    public List<Persona> getAllPersonas() {
        return personaRepository.findAll();
    }

    public Persona savePersona(Persona persona) {
        return personaRepository.save(persona);
    }

    // Métodos para gestionar los empleados
    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    public Empleado saveEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    // Métodos para gestionar los jefes
    public List<Jefe> getAllJefes() {
        return jefeRepository.findAll();
    }

    public Jefe saveJefe(Jefe jefe) {
        return jefeRepository.save(jefe);
    }

    // Método para buscar empleados con sueldo superior a un valor
    public List<Empleado> getEmpleadosConSueldoMayorA(double sueldo) {
        return empleadoRepository.findBySueldoGreaterThan(sueldo);
    }
}
