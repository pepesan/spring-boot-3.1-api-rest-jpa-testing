package com.inetum.demo.services.herencia.singletable;

import com.inetum.demo.domain.herencia.singletable.Vehiculo;
import com.inetum.demo.repositories.herencia.singletable.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Operaciones polimórficas sobre Vehiculo (todos los tipos: Coche, Moto, Camion); el alta y la modificación están en el servicio de cada tipo. */
@Service
public class VehiculoService {

    private final VehiculoRepository repository;

    @Autowired
    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> findAll() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vehiculo> findById(Long id) {
        return this.repository.findById(id);
    }

    /** Borra cualquier vehiculo, sea del tipo que sea; vacío si el id no existe. */
    @Transactional
    public Optional<Vehiculo> delete(Long id) {
        return this.repository.findById(id).map(vehiculo -> {
            this.repository.delete(vehiculo);
            return vehiculo;
        });
    }
}
