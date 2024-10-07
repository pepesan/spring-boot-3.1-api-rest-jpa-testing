package com.inetum.demo.repositories;

import com.inetum.demo.domain.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository()
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    // metodo Preestablecidos
    public List<Alumno> findByNombre(String name);
    public List<Alumno> findByEdad(Integer edad);
    public List<Alumno> findByApellidos(String apellidos);
    public List<Alumno> findByApellidosAndNombre(String apellidos, String nombre);

    // Métodos mediante Query (hql)
    @Query("SELECT a FROM Alumno a WHERE a.nombre = :name")
    List<Alumno> findAlumnosByName(String name);

    // Uso de Named Query
    List<Alumno> searchByNamedQueryName(@Param("name") String name);
}
