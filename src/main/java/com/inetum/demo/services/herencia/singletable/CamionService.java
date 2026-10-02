package com.inetum.demo.services.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Camion;
import com.inetum.demo.dtos.herencia.singletable.CamionDTO;
import com.inetum.demo.repositories.herencia.singletable.CamionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Camion (subclase de Vehiculo). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en VehiculoService.
 */
@Service
public class CamionService {

    private final CamionRepository repository;

    @Autowired
    public CamionService(CamionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Camion> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Camion> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Camion create(CamionDTO dto) {
        Camion camion = new Camion();
        dto.applyTo(camion);
        return this.repository.save(camion);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Camion> update(Long id, CamionDTO dto) {
        return this.repository.findById(id).map(camion -> {
            dto.applyTo(camion);
            return this.repository.save(camion);
        });
    }
}
