package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Empleado;
import com.inetum.demo.dtos.herencia.joined.EmpleadoDTO;
import com.inetum.demo.repositories.herencia.joined.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Empleado (subclase de Persona) (un Jefe también es un Empleado). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en PersonaService.
 */
@Service("empleadoHerenciaService") // evita el choque de nombre con services.criteria.EmpleadoService
public class EmpleadoService {

    private final EmpleadoRepository repository;

    @Autowired
    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Empleado> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empleado> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Empleado> findBySueldoMayorA(double sueldo) {
        return this.repository.findBySueldoGreaterThan(sueldo);
    }

    @Transactional
    public Empleado create(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        dto.applyTo(empleado);
        return this.repository.save(empleado);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Empleado> update(Long id, EmpleadoDTO dto) {
        return this.repository.findById(id).map(empleado -> {
            dto.applyTo(empleado);
            return this.repository.save(empleado);
        });
    }
}
