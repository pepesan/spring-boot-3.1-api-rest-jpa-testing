package com.inetum.demo.integration;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
// Para que use la config del fichero de propiedades e pruebas
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlumnoRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;

    @Test
    void testComponents(){
        assertNotNull(testEntityManager);
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
    }

    @Autowired
    private AlumnoRepository alumnoRepository;

    @BeforeEach
    void setUp() {
        this.alumnoRepository.deleteAll();
    }

    @Test
    public void whenFindByAge() {
        // given
        Alumno alumno = new Alumno();
        alumno.setNombre("Pepe");
        alumno.setEdad(21);
        testEntityManager.persist(alumno);
        entityManager.flush();

        // when
        List<Alumno> found = alumnoRepository.findByEdad(alumno.getEdad());

        // then
        Alumno alumnoBBDD = found.get(0);
        assertEquals(alumnoBBDD.getEdad(), alumno.getEdad());
        assertEquals(alumnoBBDD.getNombre(), alumno.getNombre());
    }

    @Test
    public void whenFindByNombre() {
        // given
        Alumno alumno = new Alumno();
        alumno.setNombre("Pepe");
        alumno.setEdad(21);
        testEntityManager.persist(alumno);
        entityManager.flush();

        // when
        List<Alumno> found = alumnoRepository.findByNombre("Pepe");

        // then
        Alumno alumnoBBDD = found.get(0);
        assertEquals(alumnoBBDD.getEdad(), alumno.getEdad());
        assertEquals(alumnoBBDD.getNombre(), alumno.getNombre());
    }

    @Test
    public void whenfindAlumnosByName() {
        // given
        Alumno alumno = new Alumno();
        alumno.setNombre("Pepe");
        alumno.setEdad(21);
        testEntityManager.persist(alumno);
        entityManager.flush();

        // when
        List<Alumno> found = alumnoRepository.findAlumnosByName("Pepe");

        // then
        Alumno alumnoBBDD = found.get(0);
        assertEquals(alumnoBBDD.getEdad(), alumno.getEdad());
        assertEquals(alumnoBBDD.getNombre(), alumno.getNombre());
    }

    @Test
    public void whenSearchByNamedQueryName() {
        // given
        Alumno alumno = new Alumno();
        alumno.setNombre("Pepe");
        alumno.setEdad(21);
        testEntityManager.persist(alumno);
        entityManager.flush();

        // when
        List<Alumno> found = alumnoRepository.searchByNamedQueryName("Pepe");

        // then
        Alumno alumnoBBDD = found.get(0);
        assertEquals(alumnoBBDD.getEdad(), alumno.getEdad());
        assertEquals(alumnoBBDD.getNombre(), alumno.getNombre());
    }

}
