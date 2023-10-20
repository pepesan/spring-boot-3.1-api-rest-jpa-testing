package com.inetum.demo.herencia;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.domain.herencia.Ordenador;
import com.inetum.demo.domain.herencia.Portatil;
import com.inetum.demo.domain.herencia.Sobremesa;
import com.inetum.demo.repositories.AlumnoRepository;
import com.inetum.demo.repositories.herencia.OrdenadorRepository;
import com.inetum.demo.repositories.herencia.PortatilRepository;
import com.inetum.demo.repositories.herencia.SobremesaRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
        Ordenador ordenador = new Ordenador();
        ordenador.setModelo("9355");
        ordenador.setMarca("Dell");
        testEntityManager.persist(ordenador);
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
        assertEquals(ordenadores.size(), 3);
        List<Sobremesa> sobremesas =
                this.sobremesaRepository.findAll();
        assertEquals(sobremesas.size(), 1);
        List<Portatil> portatiles =
                this.portatilRepository.findAll();
        assertEquals(portatiles.size(), 1);
    }

}
