package com.inetum.demo.integration;


import com.inetum.demo.domain.Alumno;
import com.inetum.demo.repositories.AlumnoRepository;
import com.inetum.demo.services.AlumnoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlumnoServiceTest {
    @Mock
    private AlumnoRepository alumnoRepository;

    @InjectMocks
    private AlumnoServiceImpl alumnoService;

    private Alumno alumnoInput;
    private Alumno alumnoOutput;
    @BeforeEach
    public void setUp(){
        alumnoInput = new Alumno();
        alumnoInput.setId(1L);
        alumnoInput.setNombre("David");
        alumnoInput.setApellidos("Vaquero");
        alumnoInput.setEdad(44);
    }

    @DisplayName("Test for index method with empty list")
    @Test
    public void givenNothing_whenIndex_thenReturnListOfAlumnoObjects(){
        given(alumnoRepository.findAll()).willReturn(new ArrayList<>());
        List<Alumno> listado = this.alumnoService.findAll();
        assertEquals(new ArrayList<>(), listado);
    }

    @DisplayName("Test for save method")
    @Test
    public void givenAlumnoObject_whenAdd_thenReturnAlumnoObject(){
        alumnoOutput = new Alumno();
        alumnoOutput.setId(1L);
        alumnoOutput.setNombre("David");
        alumnoOutput.setApellidos("Vaquero");
        alumnoOutput.setEdad(44);
        given(alumnoRepository.save(alumnoInput)).willReturn(alumnoOutput);

        Alumno alumno = alumnoService.save(alumnoInput);
        assertNotNull(alumno);
        assertEquals(alumnoOutput, alumno);

    }

    @DisplayName("Test for show by id method")
    @Test
    public void givenID_whenFindByID_thenReturnAlumnoObject(){
        alumnoOutput = new Alumno();
        alumnoOutput.setId(1L);
        alumnoOutput.setNombre("David");
        alumnoOutput.setApellidos("Vaquero");
        alumnoOutput.setEdad(44);
        given(alumnoRepository.findById(1L)).willReturn(Optional.ofNullable(alumnoOutput));
        Optional<Alumno> alumno = this.alumnoService.findById(1L);
        assertEquals(alumnoOutput, alumno.get());
    }

    @DisplayName("Test for update by id method")
    @Test
    public void givenIDAndAlumnoObject_whenUpdateByID_thenReturnAlumnoObject(){
        alumnoOutput = new Alumno();
        alumnoOutput.setId(1L);
        alumnoOutput.setNombre("David");
        alumnoOutput.setApellidos("Vaquero");
        alumnoOutput.setEdad(44);
        given(alumnoRepository.findById(1L)).willReturn(Optional.ofNullable(alumnoOutput));
        given(alumnoRepository.save(alumnoOutput)).willReturn(alumnoOutput);
        Alumno optionalAlumno = this.alumnoService.save( alumnoInput);
        assertEquals(alumnoOutput, optionalAlumno);
    }

    @DisplayName("Test for delete by id method")
    @Test
    public void givenID_whenDeleteByID_thenReturnAlumnoObject(){
        alumnoOutput = new Alumno();
        alumnoOutput.setId(1L);
        alumnoOutput.setNombre("David");
        alumnoOutput.setApellidos("Vaquero");
        alumnoOutput.setEdad(44);
        given(alumnoRepository.findById(1L)).willReturn(Optional.ofNullable(alumnoOutput));
        Alumno alumno = this.alumnoService.remove(alumnoInput);
        assertEquals(alumnoOutput, alumno);
    }
}
