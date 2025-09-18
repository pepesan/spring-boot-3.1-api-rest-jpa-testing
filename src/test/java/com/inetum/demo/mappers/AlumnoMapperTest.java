package com.inetum.demo.mappers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlumnoMapperTest {

    private AlumnoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AlumnoMapper();
    }

    @Test
    void toDto_mapeaTodosLosCampos() {
        // given
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Ana");
        alumno.setApellidos("López");
        alumno.setEdad(25);

        // when
        AlumnoDTO dto = mapper.toDto(alumno);

        // then
        assertNotNull(dto);
        assertEquals("Ana", dto.getNombre());
        assertEquals("López", dto.getApellidos());
        assertEquals(25, dto.getEdad());
    }

    @Test
    void toEntity_mapeaTodosLosCampos() {
        // given
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Luis");
        dto.setApellidos("Martín");
        dto.setEdad(30);

        // when
        Alumno entity = mapper.toEntity(dto);

        // then
        assertNotNull(entity);
        assertEquals("Luis", entity.getNombre());
        assertEquals("Martín", entity.getApellidos());
        assertEquals(30, entity.getEdad());
    }

    @Test
    void toDto_conNull_retornaNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.toDto(null));
    }

    @Test
    void toEntity_conNull_retornaNull() {
        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(null));
    }
}

