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
// Define que no se deberían hacer mockeos por encima de los necesarios
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
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

        // situación inicial
        // configurar que el repositorio nos devuelva un valor controlado ante una llamada a un métoido
        given(alumnoRepository.findAll()).willReturn(new ArrayList<>());
        // when
        // hago la llamada al servicio con un valor
        // el repositorio va a devolver lo que le hemos dicho antes
        List<Alumno> listado = this.alumnoService.findAll();
        // comprobamos que el servicio ante una entrada específica devuelve un valor controlado
        assertEquals(new ArrayList<>(), listado);
    }

    @DisplayName("Test for save method")
    @Test
    public void givenAlumnoObject_whenAdd_thenReturnAlumnoObject(){
        // configuramos el objeto que debería devolver
        alumnoOutput = new Alumno();
        alumnoOutput.setId(1L);
        alumnoOutput.setNombre("David");
        alumnoOutput.setApellidos("Vaquero");
        alumnoOutput.setEdad(44);
        // definimos lo que el repositorio devería devolver
        given(alumnoRepository.save(alumnoInput)).willReturn(alumnoOutput);
        // when
        // hacemos la llamada al servicio
        Alumno alumno = alumnoService.save(alumnoInput);
        // then
        // comprobamos que el dato que nos devuelve es el esperado
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
