package com.inetum.demo.services;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
