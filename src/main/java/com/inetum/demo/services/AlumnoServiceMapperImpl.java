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
    @Autowired
    AlumnoServiceMapperImpl(AlumnoRepository alumnoRepository){
        this.alumnoRepository = alumnoRepository;
    }


    public List<Alumno> findAll() {
        return this.alumnoRepository.findAll();
    }

    public Alumno save(AlumnoDTO alumnoDTO) {
        Alumno alumno = AlumnoMapper.INSTANCE.toEntity(alumnoDTO);
        this.alumnoRepository.save(alumno);
        return alumno;
    }
    public Alumno update(AlumnoDTO alumnoDTO, Long id) {
        Alumno alumno = this.alumnoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Not found with id = " + id
                ));
        Alumno entidad = AlumnoMapper.INSTANCE.toEntity(alumnoDTO);
        alumno.setId(id);
        this.alumnoRepository.save(entidad);
        return alumno;
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
        PageRequest pageRequest = PageRequest.of(page, num);
        // de manera ascendente dependiendo del campo nombre
        // pageRequest.withSort(Sort.Direction.valueOf("ASC"), "nombre");
        // otros métodos interesantes son
        // pageRequest.withSort(Sort.by("nombre"));
        // pageRequest.withSort(Sort.by("nombre").descending());
        // pageRequest.withSort(Sort.by("nombre").ascending());
        // Usando ordenación por dos campos
        pageRequest.withSort(Sort.by("nombre").descending().and(Sort.by("edad")));

        return this.alumnoRepository.findAll(pageRequest);
    }
}

