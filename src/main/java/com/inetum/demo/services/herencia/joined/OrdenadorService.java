package com.inetum.demo.services.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Ordenador;
import com.inetum.demo.repositories.herencia.joined.OrdenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Operaciones polimórficas sobre Ordenador (todos los tipos: Portatil, Sobremesa); el alta y la modificación están en el servicio de cada tipo. */
@Service
public class OrdenadorService {

    private final OrdenadorRepository repository;

    @Autowired
    public OrdenadorService(OrdenadorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Ordenador> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Ordenador> findById(Long id) {
        return this.repository.findById(id);
    }

    /** Borra cualquier ordenador, sea del tipo que sea; vacío si el id no existe. */
    @Transactional
    public Optional<Ordenador> delete(Long id) {
        return this.repository.findById(id).map(ordenador -> {
            this.repository.delete(ordenador);
            return ordenador;
        });
    }
}
