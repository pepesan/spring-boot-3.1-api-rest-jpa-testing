package com.inetum.demo.repositories.clientes;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Versión asíncrona de {@link AlumnoJdkHttpApiRepository}: {@link HttpClient#sendAsync} no bloquea el
 * hilo que llama, devuelve un {@link CompletableFuture} y la respuesta se transforma encadenando
 * {@code thenApply}. Es el equivalente del JDK a los Mono/Flux de {@link AlumnoWebClientApiRepository}.
 * La app es MVC (bloqueante), así que al final se hace {@code join()}; en una app asíncrona se
 * devolverían directamente los CompletableFuture (o se adaptarían con {@code Mono.fromFuture}).
 * <p>
 * Solo el 404 es una respuesta esperada ("no existe" → {@code Optional.empty()}); cualquier otro
 * código que no sea 2xx se convierte en {@link IllegalStateException}.
 */
@Repository("alumnoJdkHttpAsyncApiRepository")
public class AlumnoJdkHttpAsyncApiRepository implements AlumnoApiRepository {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String JSON = "application/json";
    private static final String MERGE_PATCH = "application/merge-patch+json";

    private final HttpClient httpClient;
    private final JsonMapper mapper;
    private final AlumnoApiBaseUrl baseUrl;

    public AlumnoJdkHttpAsyncApiRepository(HttpClient alumnosJdkHttpClient, JsonMapper mapper,
                                           AlumnoApiBaseUrl baseUrl) {
        this.httpClient = alumnosJdkHttpClient;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Alumno> findAll() {
        return esperar(enviar(peticion("/").GET())
                .thenApply(r -> mapper.readValue(exito(r).body(), new TypeReference<List<Alumno>>() {})));
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return esperar(enviar(peticion("/" + id).GET()).thenApply(this::leerAlumno));
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        return esperar(enviar(peticion("/")
                .header("Content-Type", JSON)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(alumno))))
                .thenApply(r -> mapper.readValue(exito(r).body(), Alumno.class)));
    }

    @Override
    public Optional<Alumno> update(Long id, AlumnoDTO alumno) {
        return esperar(enviar(peticion("/" + id)
                .header("Content-Type", JSON)
                .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(alumno))))
                .thenApply(this::leerAlumno));
    }

    @Override
    public Optional<Alumno> patch(Long id, JsonNode patch) {
        return esperar(enviar(peticion("/" + id)
                .header("Content-Type", MERGE_PATCH)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(patch))))
                .thenApply(this::leerAlumno));
    }

    @Override
    public Optional<Alumno> delete(Long id) {
        return esperar(enviar(peticion("/" + id).DELETE()).thenApply(this::leerAlumno));
    }

    private HttpRequest.Builder peticion(String ruta) {
        return HttpRequest.newBuilder(URI.create(baseUrl.alumnos() + ruta))
                .timeout(TIMEOUT)
                .header("Accept", JSON);
    }

    private CompletableFuture<HttpResponse<String>> enviar(HttpRequest.Builder peticion) {
        return httpClient.sendAsync(peticion.build(), HttpResponse.BodyHandlers.ofString());
    }

    /** 404 → vacío; 2xx → alumno del cuerpo; cualquier otro código → excepción. */
    private Optional<Alumno> leerAlumno(HttpResponse<String> respuesta) {
        if (respuesta.statusCode() == 404) {
            return Optional.empty();
        }
        return Optional.of(mapper.readValue(exito(respuesta).body(), Alumno.class));
    }

    private static HttpResponse<String> exito(HttpResponse<String> respuesta) {
        if (respuesta.statusCode() / 100 != 2) {
            throw new IllegalStateException("El API de alumnos respondió " + respuesta.statusCode()
                    + ": " + respuesta.body());
        }
        return respuesta;
    }

    /** Punto donde la cadena asíncrona se hace síncrona; se desenvuelve la excepción original. */
    private static <T> T esperar(CompletableFuture<T> futuro) {
        try {
            return futuro.join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException causa) {
                throw causa;
            }
            throw new IllegalStateException("Error llamando al API de alumnos", e.getCause());
        }
    }
}
