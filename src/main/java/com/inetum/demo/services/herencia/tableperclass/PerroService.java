package com.inetum.demo.services.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Perro;
import com.inetum.demo.dtos.herencia.tableperclass.PerroDTO;
import com.inetum.demo.repositories.herencia.tableperclass.PerroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Perro (subclase de Animal). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en AnimalService.
 */
@Service
public class PerroService {

    private final PerroRepository repository;

    @Autowired
    public PerroService(PerroRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Perro> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Perro> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Perro create(PerroDTO dto) {
        Perro perro = new Perro();
        dto.applyTo(perro);
        return this.repository.save(perro);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Perro> update(Long id, PerroDTO dto) {
        return this.repository.findById(id).map(perro -> {
            dto.applyTo(perro);
            return this.repository.save(perro);
        });
    }
}
