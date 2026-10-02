package com.inetum.demo.services.criteria;

import com.inetum.demo.domain.herencia.joined.Empleado;
import com.inetum.demo.repositories.herencia.joined.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EmpleadoServiceTest {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void limpiar() {
        empleadoRepository.deleteAll();
        Empleado empleado = new Empleado();
        empleado.setNombre("Sonia");
        empleado.setEdad(28);
        empleado.setDni("1S");
        empleado.setSueldo(21000);
        empleadoRepository.save(empleado);
    }

    @Test
    void buscarSoloConNombre() {
        List<Empleado> resultado = empleadoService.buscarEmpleadosConCriteria("Sonia", null);
        assertThat(resultado).extracting(Empleado::getNombre).contains("Sonia");
    }

    @Test
    void buscarSoloConEdad() {
        List<Empleado> resultado = empleadoService.buscarEmpleadosConCriteria(null, 28);
        assertThat(resultado).extracting(Empleado::getNombre).contains("Sonia");
    }

    @Test
    void buscarSinCriterioDevuelveListaSinFiltrar() {
        List<Empleado> resultado = empleadoService.buscarEmpleadosConCriteria(null, null);
        assertThat(resultado).isNotEmpty();
    }
}
