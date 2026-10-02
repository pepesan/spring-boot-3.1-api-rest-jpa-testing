package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Persona;
import com.inetum.demo.repositories.herencia.joined.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Operaciones polimórficas sobre Persona (todos los tipos: Empleado, Jefe); el alta y la modificación están en el servicio de cada tipo. */
@Service
public class PersonaService {

    private final PersonaRepository repository;

    @Autowired
    public PersonaService(PersonaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Persona> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Persona> findById(Long id) {
        return this.repository.findById(id);
    }

    /** Borra cualquier persona, sea del tipo que sea; vacío si el id no existe. */
    @Transactional
    public Optional<Persona> delete(Long id) {
        return this.repository.findById(id).map(persona -> {
            this.repository.delete(persona);
            return persona;
        });
    }
}
