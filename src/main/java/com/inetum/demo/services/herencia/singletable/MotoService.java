package com.inetum.demo.services.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Moto;
import com.inetum.demo.dtos.herencia.singletable.MotoDTO;
import com.inetum.demo.repositories.herencia.singletable.MotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Moto (subclase de Vehiculo). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en VehiculoService.
 */
@Service
public class MotoService {

    private final MotoRepository repository;

    @Autowired
    public MotoService(MotoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Moto> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Moto> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Moto create(MotoDTO dto) {
        Moto moto = new Moto();
        dto.applyTo(moto);
        return this.repository.save(moto);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Moto> update(Long id, MotoDTO dto) {
        return this.repository.findById(id).map(moto -> {
            dto.applyTo(moto);
            return this.repository.save(moto);
        });
    }
}
