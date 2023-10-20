package com.inetum.demo.integration;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@Slf4j
public class NewAlumnoRepositoryIntegrationTest {

    @Autowired
    AlumnoRepository alumnoRepository;

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setup(){
        // given
        Alumno alumno = new Alumno();
        alumno.setNombre("Pepe");
        alumno.setEdad(21);
        testEntityManager.persist(alumno);
        alumno = new Alumno();
        alumno.setNombre("David");
        alumno.setEdad(45);
        testEntityManager.persist(alumno);
        entityManager.flush();
    }
    @AfterEach
    public void cleanUp(){
        this.alumnoRepository.deleteAll();
        this.alumnoRepository.flush();
    }

    @Test
    public void findAllReturnAllData(){
        List<Alumno> alumnos = this.alumnoRepository.findAll();
        assertThat(alumnos).hasSize(2);
    }

    @Test
    public void findByNombre(){
        List<Alumno> alumnos = this.alumnoRepository.findByNombre("David");
        assertThat(alumnos).hasSize(1);
        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("David");
    }

    @Test
    public void findByEdad(){
        List<Alumno> alumnos = this.alumnoRepository.findByEdad(45);
        assertThat(alumnos).hasSize(1);
        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("David");
    }

}
