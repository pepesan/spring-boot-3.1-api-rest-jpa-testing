package com.inetum.demo.repositories;

import com.inetum.demo.domain.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    public List<Alumno> findByNombre(String name);
    public List<Alumno> findByEdad(Integer edad);

    @Query("SELECT a FROM Alumno a WHERE a.nombre = :name")
    List<Alumno> findAlumnosByName(String name);

    List<Alumno> searchByNamedQueryName(@Param("name") String name);
}
