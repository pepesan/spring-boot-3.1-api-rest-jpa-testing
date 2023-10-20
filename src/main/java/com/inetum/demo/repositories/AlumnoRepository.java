package com.inetum.demo.repositories;

import com.inetum.demo.domain.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    public List<Alumno> findByNombre(String name);
    public List<Alumno> findByEdad(Integer edad);
}
