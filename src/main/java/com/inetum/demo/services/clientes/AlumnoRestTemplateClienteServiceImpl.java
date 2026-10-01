package com.inetum.demo.services.clientes;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.repositories.clientes.AlumnoApiRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("alumnoRestTemplateClienteService")
public class AlumnoRestTemplateClienteServiceImpl implements AlumnoClienteService {

    private final AlumnoApiRepository repository;

    public AlumnoRestTemplateClienteServiceImpl(
            @Qualifier("alumnoRestTemplateApiRepository") AlumnoApiRepository repository) {
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
    public Optional<Alumno> remove(Long id) {
        return repository.delete(id);
    }
}
