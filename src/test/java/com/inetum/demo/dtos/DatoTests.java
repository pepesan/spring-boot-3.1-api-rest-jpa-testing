package com.inetum.demo.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatoTests {
    @Test
    public void testDatoCreation() {
        Dato dato = new Dato();
        dato.setId(1L);
        dato.setCadena("TestString");

        assertEquals(1L, dato.getId());
        assertEquals("TestString", dato.getCadena());
    }
    // test the AllArgsConstructor
    @Test
    public void testDatoAllArgsConstructor() {
        Dato dato = new Dato(1L, "TestString");

        assertEquals(1L, dato.getId());
        assertEquals("TestString", dato.getCadena());
    }
}
