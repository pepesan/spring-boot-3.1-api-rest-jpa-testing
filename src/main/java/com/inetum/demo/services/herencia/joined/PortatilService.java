package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Portatil;
import com.inetum.demo.dtos.herencia.joined.PortatilDTO;
import com.inetum.demo.repositories.herencia.joined.PortatilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Portatil (subclase de Ordenador). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en OrdenadorService.
 */
@Service
public class PortatilService {

    private final PortatilRepository repository;

    @Autowired
    public PortatilService(PortatilRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Portatil> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Portatil> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Portatil create(PortatilDTO dto) {
        Portatil portatil = new Portatil();
        dto.applyTo(portatil);
        return this.repository.save(portatil);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Portatil> update(Long id, PortatilDTO dto) {
        return this.repository.findById(id).map(portatil -> {
            dto.applyTo(portatil);
            return this.repository.save(portatil);
        });
    }
}
