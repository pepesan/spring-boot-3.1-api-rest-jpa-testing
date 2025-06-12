package com.inetum.demo.domain;

import com.inetum.demo.dtos.AlumnoDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AlumnoTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenNoArgsConstructor_thenDefaultsAreZeroOrEmpty() {
        Alumno a = new Alumno();
        assertEquals(0L, a.getId());
        assertEquals("", a.getNombre());
        assertEquals("", a.getApellidos());
        assertEquals(0, a.getEdad());
    }

    @Test
    public void whenDTOConstructor_thenFieldsAreMapped() {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Carlos");
        dto.setApellidos("García");
        dto.setEdad(25);

        Alumno a = new Alumno(dto);
        assertNull(a.getId(), "El id no debería asignarse en el constructor DTO");
        assertEquals("Carlos", a.getNombre());
        assertEquals("García", a.getApellidos());
        assertEquals(25, a.getEdad());
    }

    @Test
    public void givenValidAlumno_whenValidate_thenNoViolations() {
        Alumno a = new Alumno();
        a.setNombre("María");
        a.setApellidos("López");
        a.setEdad(30);

        Set<ConstraintViolation<Alumno>> violations = validator.validate(a);
        assertTrue(violations.isEmpty(),
                () -> "Se esperaban cero violaciones pero se encontraron: " + violations);
    }

    @Test
    public void givenNombreTooShort_whenValidate_thenSizeViolation() {
        Alumno a = new Alumno();
        a.setNombre("Al");              // menos de 3 caracteres
        a.setApellidos("Pérez");
        a.setEdad(20);

        Set<ConstraintViolation<Alumno>> violations = validator.validate(a);
        assertFalse(violations.isEmpty());
        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")
                        && v.getMessage().contains("mas de 3"));
        assertTrue(found, "Debe haber una violación por tamaño de nombre");
    }

    @Test
    public void givenNombreTooLong_whenValidate_thenSizeViolation() {
        Alumno a = new Alumno();
        a.setNombre("A".repeat(25));    // más de 20 caracteres
        a.setApellidos("Pérez");
        a.setEdad(22);

        Set<ConstraintViolation<Alumno>> violations = validator.validate(a);
        assertFalse(violations.isEmpty());
        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("nombre")
                        && v.getMessage().contains("menos de 20"));
        assertTrue(found, "Debe haber una violación por tamaño de nombre excesivo");
    }

    @Test
    public void givenEdadTooLow_whenValidate_thenMinViolation() {
        Alumno a = new Alumno();
        a.setNombre("Rubén");
        a.setApellidos("Martín");
        a.setEdad(16);                  // menor que 18

        Set<ConstraintViolation<Alumno>> violations = validator.validate(a);
        assertFalse(violations.isEmpty());
        boolean found = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("edad")
                        && v.getMessage().contains("18+"));
        assertTrue(found, "Debe haber una violación porque la edad es menor que 18");
    }
}

