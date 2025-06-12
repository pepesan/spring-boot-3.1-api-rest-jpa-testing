package com.inetum.demo.services;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AlumnoServiceMapper {
    public List<Alumno> findAll();
    public Alumno save(AlumnoDTO alumno);
    public Alumno findById(Long id);
    public Alumno update(AlumnoDTO alumno, Long id);
    public Alumno remove(Long id);

    Page<Alumno> findAllPageable(int page, int num);
}
