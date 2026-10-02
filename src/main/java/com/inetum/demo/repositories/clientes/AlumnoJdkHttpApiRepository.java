package com.inetum.demo.repositories.clientes;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Misma consulta que {@link AlumnoRestTemplateApiRepository} con el {@link HttpClient} del JDK
 * ({@code java.net.http}), sin ninguna ayuda de Spring: aquí se construye cada petición, se
 * serializa/deserializa el JSON a mano con Jackson y se interpreta el código de estado.
 * <p>
 * Solo el 404 es una respuesta esperada ("no existe" → {@code Optional.empty()}); cualquier otro
 * código que no sea 2xx se convierte en {@link IllegalStateException}.
 */
@Repository("alumnoJdkHttpApiRepository")
public class AlumnoJdkHttpApiRepository implements AlumnoApiRepository {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String JSON = "application/json";
    private static final String MERGE_PATCH = "application/merge-patch+json";

    private final HttpClient httpClient;
    private final JsonMapper mapper;
    private final AlumnoApiBaseUrl baseUrl;

    public AlumnoJdkHttpApiRepository(HttpClient alumnosJdkHttpClient, JsonMapper mapper, AlumnoApiBaseUrl baseUrl) {
        this.httpClient = alumnosJdkHttpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Alumno> findAll() {
        HttpResponse<String> respuesta = enviar(peticion("/").GET());
        comprobarExito(respuesta);
        return mapper.readValue(respuesta.body(), new TypeReference<List<Alumno>>() {});
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return leerAlumno(enviar(peticion("/" + id).GET()));
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        HttpResponse<String> respuesta = enviar(peticion("/")
                .header("Content-Type", JSON)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(alumno))));
        comprobarExito(respuesta);
        return mapper.readValue(respuesta.body(), Alumno.class);
    }

    @Override
    public Optional<Alumno> update(Long id, AlumnoDTO alumno) {
        return leerAlumno(enviar(peticion("/" + id)
                .header("Content-Type", JSON)
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(alumno)))));
    }

    @Override
    public Optional<Alumno> patch(Long id, JsonNode patch) {
        // El JDK no tiene un método patch(): se usa method("PATCH", ...).
        return leerAlumno(enviar(peticion("/" + id)
                .header("Content-Type", MERGE_PATCH)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(patch)))));
    }

    @Override
    public Optional<Alumno> delete(Long id) {
        return leerAlumno(enviar(peticion("/" + id).DELETE()));
    }

    private HttpRequest.Builder peticion(String ruta) {
        return HttpRequest.newBuilder(URI.create(baseUrl.alumnos() + ruta))
                .timeout(TIMEOUT)
                .header("Accept", JSON);
    }

    private HttpResponse<String> enviar(HttpRequest.Builder peticion) {
        try {
            return httpClient.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("Error de E/S llamando al API de alumnos", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();   // se conserva la señal de interrupción
            throw new IllegalStateException("Llamada al API de alumnos interrumpida", e);
        }
    }

    /** 404 → vacío; 2xx → alumno del cuerpo; cualquier otro código → excepción. */
    private Optional<Alumno> leerAlumno(HttpResponse<String> respuesta) {
        if (respuesta.statusCode() == 404) {
            return Optional.empty();
        }
        comprobarExito(respuesta);
        return Optional.of(mapper.readValue(respuesta.body(), Alumno.class));
    }

    private static void comprobarExito(HttpResponse<String> respuesta) {
        if (respuesta.statusCode() / 100 != 2) {
            throw new IllegalStateException("El API de alumnos respondió " + respuesta.statusCode()
                    + ": " + respuesta.body());
        }
    }
}
