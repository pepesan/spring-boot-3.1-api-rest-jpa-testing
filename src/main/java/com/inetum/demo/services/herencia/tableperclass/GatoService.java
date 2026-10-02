package com.inetum.demo.services.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Gato;
import com.inetum.demo.dtos.herencia.tableperclass.GatoDTO;
import com.inetum.demo.repositories.herencia.tableperclass.GatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Gato (subclase de Animal). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en AnimalService.
 */
@Service
public class GatoService {

    private final GatoRepository repository;

    @Autowired
    public GatoService(GatoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Gato> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Gato> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Gato create(GatoDTO dto) {
        Gato gato = new Gato();
        dto.applyTo(gato);
        return this.repository.save(gato);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Gato> update(Long id, GatoDTO dto) {
        return this.repository.findById(id).map(gato -> {
            dto.applyTo(gato);
            return this.repository.save(gato);
        });
    }
}
