package com.inetum.demo.services;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.mappers.AlumnoMapper;
import com.inetum.demo.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoServiceMapperImpl implements AlumnoServiceMapper{

    private AlumnoRepository alumnoRepository;
    private final AlumnoMapper mapper;

    @Autowired
    public AlumnoServiceMapperImpl(AlumnoRepository repo, AlumnoMapper mapper) {
        this.alumnoRepository = repo;
        this.mapper = mapper;
    }


    public List<Alumno> findAll() {
        return this.alumnoRepository.findAll();
    }

    @Override
    public Alumno save(AlumnoDTO dto) {
        Alumno entity = mapper.toEntity(dto);
        return alumnoRepository.save(entity);
    }
    @Override
    public Alumno update(AlumnoDTO dto, Long id) {
        Alumno existente = alumnoRepository.findById(id).orElseThrow();

        // Copia explícita de campos desde el DTO (no hay id en el DTO)
        existente.setNombre(dto.getNombre());
        existente.setApellidos(dto.getApellidos());
        existente.setEdad(dto.getEdad());

        return alumnoRepository.save(existente);
    }

    public Alumno findById(Long id) {
        return this.alumnoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
    }

    public Alumno remove(Long id) {
        Alumno alumno = this.alumnoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        this.alumnoRepository.delete(alumno);
        return alumno;
    }

    @Override
    public Page<Alumno> findAllPageable(int page, int num) {
        // PageRequest es inmutable: withSort(...) devuelve una instancia nueva, hay que recogerla.
        PageRequest pageRequest = PageRequest.of(page, num)
                .withSort(Sort.by("nombre").descending().and(Sort.by("edad")));

        return this.alumnoRepository.findAll(pageRequest);
    }
}

