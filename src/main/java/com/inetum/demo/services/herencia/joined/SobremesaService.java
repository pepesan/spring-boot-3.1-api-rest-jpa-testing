package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Sobremesa;
import com.inetum.demo.dtos.herencia.joined.SobremesaDTO;
import com.inetum.demo.repositories.herencia.joined.SobremesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * CRUD de Sobremesa (subclase de Ordenador). Al usar su propio repositorio, un id de otro tipo cuenta como
 * "no encontrado". El borrado está en OrdenadorService.
 */
@Service
public class SobremesaService {

    private final SobremesaRepository repository;

    @Autowired
    public SobremesaService(SobremesaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Sobremesa> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Sobremesa> findById(Long id) {
        return this.repository.findById(id);
    }

    @Transactional
    public Sobremesa create(SobremesaDTO dto) {
        Sobremesa sobremesa = new Sobremesa();
        dto.applyTo(sobremesa);
        return this.repository.save(sobremesa);
    }

    /** Sustitución completa (PUT); vacío si el id no existe. */
    @Transactional
    public Optional<Sobremesa> update(Long id, SobremesaDTO dto) {
        return this.repository.findById(id).map(sobremesa -> {
            dto.applyTo(sobremesa);
            return this.repository.save(sobremesa);
        });
    }
}
