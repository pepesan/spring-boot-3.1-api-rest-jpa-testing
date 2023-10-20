package com.inetum.demo.integration;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
public class NewAlumnoRepositoryIntegrationTest {

    @Autowired
    AlumnoRepository alumnoRepository;

    @BeforeEach
    void setup(){
        this.alumnoRepository.saveAllAndFlush(List.of(
                new Alumno(1L, "David", "Vaquero", 45),
                new Alumno(2L, "Nuria", "Martín", 32)
        ));
    }

    @Test
    public void findAllReturnAllData(){
        List<Alumno> alumnos = this.alumnoRepository.findAll();
        assertThat(alumnos).hasSize(2);
    }

    @Test
    public void findByNameRetreiveOneEntry(){
        List<Alumno> alumnos = this.alumnoRepository.findByNombre("David");
        assertThat(alumnos).hasSize(1);
        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("David");
    }
    @Test
    public void findByNombreContainsOneEntry(){
        List<Alumno> alumnos = this.alumnoRepository.findByNombreContains("Dav");
        assertThat(alumnos).hasSize(1);
        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("David");
    }
    @Test
    public void findByNombreContainsIgnoreCase(){
        List<Alumno> alumnos = this.alumnoRepository.findByNombreContainsIgnoreCase("dav");
        assertThat(alumnos).hasSize(1);
        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("David");
    }

}
