package com.inetum.demo.services.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.repositories.clientes.AlumnoApiRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Service("alumnoJdkHttpAsyncClienteService")
public class AlumnoJdkHttpAsyncClienteServiceImpl implements AlumnoClienteService {

    private final AlumnoApiRepository repository;

    public AlumnoJdkHttpAsyncClienteServiceImpl(
            @Qualifier("alumnoJdkHttpAsyncApiRepository") AlumnoApiRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Alumno> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        return repository.create(alumno);
    }

    @Override
    public Optional<Alumno> update(Long id, AlumnoDTO alumno) {
        return repository.update(id, alumno);
    }

    @Override
    public Optional<Alumno> patch(Long id, JsonNode patch) {
        return repository.patch(id, patch);
    }

    @Override
    public Optional<Alumno> remove(Long id) {
        return repository.delete(id);
    }
}
