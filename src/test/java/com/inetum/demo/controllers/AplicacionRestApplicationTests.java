package com.inetum.demo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static
        org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
class AplicacionRestApplicationTests {
    // Haciendo una Injección de Dependencias del Controlador en esta clase
    // definir un atributo en la clase que se va a auto cargar
    @Autowired
    private MiRestController controller;
    // prueba sobre el controlador
    @Test
    public void miRestControllerLoads() throws Exception {
    // el controlador no es null, es decir carga correctamente
        assertNotNull(controller);
        assertThat(controller).isNotNull();
    }
    // Cargar el gestor de peticiones a la aplicación Web
    // hace una especie de Mock que carga la aplicación y hace peticiones
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void shouldReturnHello() throws Exception {
        // realizar una petición
        this.mockMvc.perform(
                // hacer un métódo get en la petición
                // indicando la ruta de acceso
                get("/")
                // fin de la llamada a perform
                )
                // imprimir por pantalla el resultado
                .andDo(print())
                // comprobamos que el status es 200 OK
                .andExpect(status().isOk())
                // comprobamos que el contenido es lo que esperamos
                .andExpect(content().string(containsString("Hola Mundo")));
    }
}
