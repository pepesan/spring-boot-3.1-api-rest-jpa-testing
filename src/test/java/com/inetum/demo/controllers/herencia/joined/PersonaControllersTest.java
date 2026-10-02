package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.repositories.herencia.joined.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Herencia JOINED en varios niveles: Persona -> Empleado -> Jefe, con CRUD por tipo y vista polimórfica. */
@SpringBootTest
@AutoConfigureMockMvc
class PersonaControllersTest {

    private static final String EMPLEADO = "{\"nombre\":\"Ana\",\"direccion\":\"Calle Mayor\",\"edad\":30,\"dni\":\"1A\",\"sueldo\":25000}";
    private static final String JEFE = "{\"nombre\":\"Carlos\",\"edad\":50,\"dni\":\"4D\",\"sueldo\":50000,\"departamento\":\"IT\"}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiar() {
        personaRepository.deleteAll();
    }

    private long crear(String ruta, String json) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/" + ruta + "/")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void cadaNivelGuardaSusCamposEnSuTabla() throws Exception {
        long idJefe = crear("jefes", JEFE);
        crear("empleados", EMPLEADO);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persona", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM empleado", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM jefe", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT departamento FROM jefe WHERE emp_id = ?", String.class, idJefe)).isEqualTo("IT");
    }

    @Test
    void personasListaTodosLosTiposConSuCampoTipo() throws Exception {
        crear("empleados", EMPLEADO);
        crear("jefes", JEFE);

        mockMvc.perform(get("/api/v1/personas/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.tipo == 'EMPLEADO')].nombre").value("Ana"))
                .andExpect(jsonPath("$[?(@.tipo == 'JEFE')].departamento").value("IT"));
    }

    @Test
    void personasShowYDeleteFuncionanConCualquierTipo() throws Exception {
        long id = crear("jefes", JEFE);

        mockMvc.perform(get("/api/v1/personas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("JEFE"));
        mockMvc.perform(delete("/api/v1/personas/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/personas/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/personas/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void unJefeEsUnEmpleadoPeroUnEmpleadoNoEsUnJefe() throws Exception {
        long idEmpleado = crear("empleados", EMPLEADO);
        long idJefe = crear("jefes", JEFE);

        mockMvc.perform(get("/api/v1/empleados/")).andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/v1/jefes/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/empleados/" + idJefe)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/jefes/" + idEmpleado)).andExpect(status().isNotFound());
    }

    @Test
    void empleadosConSueldoMayorAFiltraCorrectamente() throws Exception {
        crear("empleados", "{\"nombre\":\"Bajo\",\"edad\":25,\"dni\":\"2B\",\"sueldo\":15000}");
        crear("empleados", "{\"nombre\":\"Alto\",\"edad\":40,\"dni\":\"3C\",\"sueldo\":35000}");

        mockMvc.perform(get("/api/v1/empleados/sueldo-mayor/20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre").value("Alto"));
    }

    @Test
    void putReemplazaLosCamposYMantieneElId() throws Exception {
        long idEmpleado = crear("empleados", EMPLEADO);
        long idJefe = crear("jefes", JEFE);

        mockMvc.perform(put("/api/v1/empleados/" + idEmpleado).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"edad\":31,\"dni\":\"1A\",\"sueldo\":27000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idEmpleado))
                .andExpect(jsonPath("$.sueldo").value(27000));
        mockMvc.perform(put("/api/v1/jefes/" + idJefe).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Carlos\",\"edad\":51,\"dni\":\"4D\",\"sueldo\":55000,\"departamento\":\"RRHH\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departamento").value("RRHH"));
        mockMvc.perform(put("/api/v1/jefes/9999").contentType(MediaType.APPLICATION_JSON).content(JEFE))
                .andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM persona", Integer.class)).isEqualTo(2);
    }

    @Test
    void datosInvalidosDevuelven400() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"dni\":\"\",\"sueldo\":-1}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/jefes/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Carlos\",\"dni\":\"4D\",\"sueldo\":1,\"departamento\":\"\"}"))
                .andExpect(status().isBadRequest());
        assertThat(personaRepository.count()).isZero();
    }
}
