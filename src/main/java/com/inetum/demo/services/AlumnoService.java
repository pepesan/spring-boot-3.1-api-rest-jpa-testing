package com.inetum.demo.services;

import com.inetum.demo.domain.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface AlumnoService {
    public List<Alumno> findAll();
    public Alumno save(Alumno alumno);
    public Optional<Alumno> findById(Long id);

    public Alumno remove(Alumno alumno);

    public Page<Alumno> findAllPageable(int page, int num);
}
