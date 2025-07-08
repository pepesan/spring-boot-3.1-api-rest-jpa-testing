package com.inetum.demo.testcontainers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @ContextConfiguration(initializers = AlumnoRepositoryTestContainersTest.DataSourceInitializer.class)
public class AlumnoRepositoryTestContainersTest {
    /*
    @Autowired
    AlumnoRepository alumnoRepository;
    @Container
    static  final MariaDBContainer<?> database =
            new MariaDBContainer<>("mariadb:latest")
                    .withDatabaseName("test")
                    .withExposedPorts(3306);
    static  class DataSourceInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext>{

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url="+database.getJdbcUrl(),
                    "spring.datasource.username="+database.getUsername(),
                    "spring.datasource.password="+database.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=create-drop"
            );
        }
    }

    @BeforeEach
    void setup(){
        this.alumnoRepository.deleteAll();
        this.alumnoRepository.saveAllAndFlush(List.of(
                new Alumno(null, "David", "Vaquero", 45),
                new Alumno(null, "Nuria", "Martín", 32)
        ));
    }

    @Test
    public void findAllReturnAllData(){
        List<Alumno> alumnos = this.alumnoRepository.findAll();
        assertThat(alumnos).hasSize(2);
    }
    */
}
