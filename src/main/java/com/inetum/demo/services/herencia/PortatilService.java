package com.inetum.demo.services.herencia;

import com.inetum.demo.domain.herencia.Portatil;
import com.inetum.demo.dtos.PortatilDTO;
import com.inetum.demo.repositories.herencia.PortatilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** CRUD de Portatil (herencia JOINED con Ordenador). */
@Service
public class PortatilService {

    private final PortatilRepository portatilRepository;

    @Autowired
    public PortatilService(PortatilRepository portatilRepository) {
        this.portatilRepository = portatilRepository;
    }

    @Transactional(readOnly = true)
    public List<Portatil> findAll() {
        return this.portatilRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Portatil> findById(Long id) {
        return this.portatilRepository.findById(id);
    }

    @Transactional
    public Portatil create(PortatilDTO dto) {
        Portatil portatil = new Portatil();
        dto.applyTo(portatil);
        return this.portatilRepository.save(portatil);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Portatil> update(Long id, PortatilDTO dto) {
        return this.portatilRepository.findById(id).map(portatil -> {
            dto.applyTo(portatil);
            return this.portatilRepository.save(portatil);
        });
    }

    /** Borra el portátil y devuelve lo borrado; vacío si el id no existe. */
    @Transactional
    public Optional<Portatil> delete(Long id) {
        return this.portatilRepository.findById(id).map(portatil -> {
            this.portatilRepository.delete(portatil);
            return portatil;
        });
    }
}
