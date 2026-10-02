package com.inetum.demo.services.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Coche;
import com.inetum.demo.dtos.herencia.singletable.CocheDTO;
import com.inetum.demo.repositories.herencia.singletable.CocheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Coche (subclase de Vehiculo). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en VehiculoService.
 */
@Service
public class CocheService {

    private final CocheRepository repository;

    @Autowired
    public CocheService(CocheRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Coche> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Coche> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Coche create(CocheDTO dto) {
        Coche coche = new Coche();
        dto.applyTo(coche);
        return this.repository.save(coche);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Coche> update(Long id, CocheDTO dto) {
        return this.repository.findById(id).map(coche -> {
            dto.applyTo(coche);
            return this.repository.save(coche);
        });
    }
}
