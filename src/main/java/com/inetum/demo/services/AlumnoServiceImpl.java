package com.inetum.demo.services;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoServiceImpl implements AlumnoService{

    private AlumnoRepository alumnoRepository;
    @Autowired
    AlumnoServiceImpl(AlumnoRepository alumnoRepository){
        this.alumnoRepository = alumnoRepository;
    }


    public List<Alumno> findAll() {
        return this.alumnoRepository.findAll();
    }

    public Alumno save(Alumno alumno) {
        this.alumnoRepository.save(alumno);
        return alumno;
    }

    public Optional<Alumno> findById(Long id) {
        return this.alumnoRepository.findById(id);
    }

    public Alumno remove(Alumno alumno) {
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
