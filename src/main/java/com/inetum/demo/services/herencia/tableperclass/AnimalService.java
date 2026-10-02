package com.inetum.demo.services.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Animal;
import com.inetum.demo.repositories.herencia.tableperclass.AnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Operaciones polimórficas sobre Animal (todos los tipos: Perro, Gato); el alta y la modificación están en el servicio de cada tipo. */
@Service
public class AnimalService {

    private final AnimalRepository repository;

    @Autowired
    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Animal> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Animal> findById(Long id) {
        return this.repository.findById(id);
    }

    /** Borra cualquier animal, sea del tipo que sea; vacío si el id no existe. */
    @Transactional
    public Optional<Animal> delete(Long id) {
        return this.repository.findById(id).map(animal -> {
            this.repository.delete(animal);
            return animal;
        });
    }
}
