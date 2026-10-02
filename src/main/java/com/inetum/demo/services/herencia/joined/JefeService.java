package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Jefe;
import com.inetum.demo.dtos.herencia.joined.JefeDTO;
import com.inetum.demo.repositories.herencia.joined.JefeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Jefe (subclase de Persona). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en PersonaService.
 */
@Service
public class JefeService {

    private final JefeRepository repository;

    @Autowired
    public JefeService(JefeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Jefe> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Jefe> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Jefe create(JefeDTO dto) {
        Jefe jefe = new Jefe();
        dto.applyTo(jefe);
        return this.repository.save(jefe);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Jefe> update(Long id, JefeDTO dto) {
        return this.repository.findById(id).map(jefe -> {
            dto.applyTo(jefe);
            return this.repository.save(jefe);
        });
    }
}
