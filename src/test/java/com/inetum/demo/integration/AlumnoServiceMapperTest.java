package com.inetum.demo.integration;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.mappers.AlumnoMapper;
import com.inetum.demo.repositories.AlumnoRepository;
import com.inetum.demo.services.AlumnoServiceMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class AlumnoServiceMapperTest {

    @Mock
    private AlumnoRepository alumnoRepository;

    // Mock del mapper: solo se stubea en los tests donde realmente se usa
    @Mock
    private AlumnoMapper alumnoMapper;

    private AlumnoServiceMapperImpl alumnoService;

    private AlumnoDTO dtoValido;

    @BeforeEach
    void setUp() {
        alumnoService = new AlumnoServiceMapperImpl(alumnoRepository, alumnoMapper);

        dtoValido = new AlumnoDTO();
        dtoValido.setNombre("David");
        dtoValido.setApellidos("Vaquero");
        dtoValido.setEdad(44);
    }

    // ---------- Helpers ----------
    private Alumno alumnoExistente(Long id, String nombre, String apellidos, Integer edad) {
        Alumno a = new Alumno();
        a.setId(id);
        a.setNombre(nombre);
        a.setApellidos(apellidos);
        a.setEdad(edad);
        return a;
    }

    // ---------- Tests ----------

    @DisplayName("findAll: devuelve lista de alumnos")
    @Test
    void givenNothing_whenFindAll_thenReturnsList() {
        Alumno a = alumnoExistente(1L, "Ana", "López", 25);
        BDDMockito.given(alumnoRepository.findAll()).willReturn(Arrays.asList(a));

        List<Alumno> result = alumnoService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ana", result.get(0).getNombre());
    }

    @DisplayName("save: usa el mapper (mock) y persiste la entidad resultante")
    @Test
    void givenAlumnoDTO_whenSave_thenReturnsPersistedAlumno() {
        // 🔧 El servicio debe llamar a alumnoMapper.toEntity(dto)
        BDDMockito.given(alumnoMapper.toEntity(dtoValido)).willAnswer(inv -> {
            AlumnoDTO dto = inv.getArgument(0);
            Alumno e = new Alumno();
            e.setNombre(dto.getNombre());
            e.setApellidos(dto.getApellidos());
            e.setEdad(dto.getEdad());
            return e;
        });

        // El repo devuelve lo que entra para evitar mismatches con STRICT_STUBS
        BDDMockito.willAnswer(inv -> inv.getArgument(0))
                .given(alumnoRepository).save(ArgumentMatchers.any(Alumno.class));

        Alumno result = alumnoService.save(dtoValido);

        assertNotNull(result);
        assertEquals("David", result.getNombre());
        assertEquals("Vaquero", result.getApellidos());
        assertEquals(44, result.getEdad());

        // Se verifican interacciones: asegura que el stub sí se usó
        verify(alumnoMapper).toEntity(dtoValido);

        // Captura para verificar lo enviado al repositorio
        ArgumentCaptor<Alumno> captor = ArgumentCaptor.forClass(Alumno.class);
        verify(alumnoRepository).save(captor.capture());
        Alumno enviado = captor.getValue();
        assertEquals("David", enviado.getNombre());
        assertEquals("Vaquero", enviado.getApellidos());
        assertEquals(44, enviado.getEdad());
    }

    @DisplayName("findById: devuelve alumno si existe")
    @Test
    void givenId_whenFindById_thenReturnsAlumno() {
        Alumno existente = alumnoExistente(1L, "Eva", "Martín", 31);
        BDDMockito.given(alumnoRepository.findById(1L)).willReturn(Optional.of(existente));

        Alumno result = alumnoService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Eva", result.getNombre());
    }

    @DisplayName("update: copia campos del DTO en existente y guarda (conserva ID)")
    @Test
    void givenAlumnoDTOAndId_whenUpdate_thenReturnsUpdatedAlumno() {
        Alumno existente = alumnoExistente(1L, "Antiguo", "Apellido", 30);

        BDDMockito.given(alumnoRepository.findById(1L)).willReturn(Optional.of(existente));
        // El repo devuelve lo que entra
        BDDMockito.willAnswer(inv -> inv.getArgument(0))
                .given(alumnoRepository).save(ArgumentMatchers.any(Alumno.class));


        Alumno actualizado = alumnoService.update(dtoValido, 1L);

        assertNotNull(actualizado);
        assertEquals(1L, actualizado.getId());
        assertEquals("David", actualizado.getNombre());
        assertEquals("Vaquero", actualizado.getApellidos());
        assertEquals(44, actualizado.getEdad());

        // Verifica que el guardado ha sido con el objeto con ID original
        ArgumentCaptor<Alumno> captor = ArgumentCaptor.forClass(Alumno.class);
        verify(alumnoRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @DisplayName("remove: elimina por id y devuelve el eliminado")
    @Test
    void givenId_whenRemove_thenDeletesAndReturnsAlumno() {
        Alumno existente = alumnoExistente(2L, "Luis", "Pérez", 28);
        BDDMockito.given(alumnoRepository.findById(2L)).willReturn(Optional.of(existente));
        BDDMockito.willDoNothing().given(alumnoRepository).delete(existente);

        Alumno eliminado = alumnoService.remove(2L);

        assertNotNull(eliminado);
        assertEquals(2L, eliminado.getId());
        assertEquals("Luis", eliminado.getNombre());

        verify(alumnoRepository).delete(existente);
    }

    @DisplayName("findAllPageable: devuelve una página de alumnos")
    @Test
    void givenPageRequest_whenFindAllPageable_thenReturnsPage() {
        Alumno a = alumnoExistente(3L, "Marta", "Gil", 22);
        Page<Alumno> page = new PageImpl<>(List.of(a));
        // Stub flexible para cualquier PageRequest
        BDDMockito.given(alumnoRepository.findAll(ArgumentMatchers.any(PageRequest.class))).willReturn(page);

        Page<Alumno> result = alumnoService.findAllPageable(0, 2);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Marta", result.getContent().get(0).getNombre());
    }
}
