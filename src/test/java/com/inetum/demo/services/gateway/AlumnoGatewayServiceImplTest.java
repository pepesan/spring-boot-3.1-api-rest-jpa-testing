package com.inetum.demo.services.gateway;

import tools.jackson.databind.json.JsonMapper;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.gateways.AlumnoGateway;
import com.inetum.demo.gateways.AlumnoGatewayException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlumnoGatewayServiceImplTest {

    private final AlumnoGateway gateway = mock(AlumnoGateway.class);
    private final AlumnoGatewayServiceImpl service = new AlumnoGatewayServiceImpl(gateway);
    private final Alumno alumno = new Alumno(1L, "Marta", "Perez", 30);

    private static AlumnoGatewayException fallo(boolean transitorio) {
        return new AlumnoGatewayException("fallo", new RuntimeException(), transitorio);
    }

    @Test
    void sinFalloDevuelveElListadoActual() {
        when(gateway.findAll()).thenReturn(List.of(alumno));

        AlumnoGatewayService.Listado listado = service.findAll();

        assertThat(listado.alumnos()).containsExactly(alumno);
        assertThat(listado.obsoleto()).isFalse();
    }

    @Test
    void siElRemotoFallaDevuelveElUltimoListadoConocidoMarcadoComoObsoleto() {
        when(gateway.findAll()).thenReturn(List.of(alumno)).thenThrow(fallo(true));
        service.findAll();

        AlumnoGatewayService.Listado listado = service.findAll();

        assertThat(listado.alumnos()).containsExactly(alumno);
        assertThat(listado.obsoleto()).isTrue();
    }

    @Test
    void sinListadoPrevioPropagaLaExcepcion() {
        when(gateway.findAll()).thenThrow(fallo(true));

        assertThatThrownBy(service::findAll).isInstanceOf(AlumnoGatewayException.class);
    }

    @Test
    void unFalloNoTransitorioNuncaUsaElFallback() {
        when(gateway.findAll()).thenReturn(List.of(alumno)).thenThrow(fallo(false));
        service.findAll();

        assertThatThrownBy(service::findAll).isInstanceOf(AlumnoGatewayException.class);
    }

    @Test
    void delegaEnElGatewayElResto() {
        AlumnoDTO dto = new AlumnoDTO();
        when(gateway.findById(1L)).thenReturn(Optional.of(alumno));
        when(gateway.create(dto)).thenReturn(alumno);
        when(gateway.delete(1L)).thenReturn(Optional.of(alumno));

        assertThat(service.findById(1L)).contains(alumno);
        assertThat(service.create(dto)).isEqualTo(alumno);
        assertThat(service.remove(1L)).contains(alumno);
    }

    @Test
    void updateYPatchDelegaNEnElGateway() {
        AlumnoDTO dto = new AlumnoDTO();
        JsonMapper mapper = new JsonMapper();
        when(gateway.update(1L, dto)).thenReturn(Optional.of(alumno));
        when(gateway.patch(2L, mapper.readTree("{}"))).thenReturn(Optional.empty());

        assertThat(service.update(1L, dto)).contains(alumno);
        assertThat(service.patch(2L, mapper.readTree("{}"))).isEmpty();
    }
}
