package com.inetum.demo.herencia;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.domain.herencia.joined.Ordenador;
import com.inetum.demo.domain.herencia.joined.Portatil;
import com.inetum.demo.domain.herencia.joined.Sobremesa;
import com.inetum.demo.repositories.AlumnoRepository;
import com.inetum.demo.repositories.herencia.joined.OrdenadorRepository;
import com.inetum.demo.repositories.herencia.joined.PortatilRepository;
import com.inetum.demo.repositories.herencia.joined.SobremesaRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Slf4j
public class HerenciaTest {

    @Autowired
    OrdenadorRepository ordenadorRepository;
    @Autowired
    SobremesaRepository sobremesaRepository;
    @Autowired
    PortatilRepository portatilRepository;

    @Autowired private TestEntityManager testEntityManager;
    @Autowired private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setup(){
        // given
        Portatil portatil = new Portatil();
        portatil.setModelo("9355");
        portatil.setMarca("Dell");
        portatil.setPeso(2.0F);
        portatil.setDuracionBateria(2);
        testEntityManager.persist(portatil);
        Sobremesa sobremesa = new Sobremesa();
        sobremesa.setMarca("Dell");
        sobremesa.setModelo("Poweredge");
        sobremesa.setTieneMonitor(false);
        sobremesa.setTipoTorre("Full");
        testEntityManager.persist(sobremesa);
        entityManager.flush();
    }

    @Test
    public void pruebaOrdenadores(){
        List<Ordenador> ordenadores =
                this.ordenadorRepository.findAll();
        assertEquals(ordenadores.size(), 2);
        List<Sobremesa> sobremesas =
                this.sobremesaRepository.findAll();
        assertEquals(sobremesas.size(), 1);
        List<Portatil> portatiles =
                this.portatilRepository.findAll();
        assertEquals(portatiles.size(), 1);
    }

}
