package com.inetum.demo.repositories.clientes;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

@Repository("alumnoRestTemplateApiRepository")
public class AlumnoRestTemplateApiRepository implements AlumnoApiRepository {

    private final RestTemplate restTemplate;
    private final AlumnoApiBaseUrl baseUrl;

    public AlumnoRestTemplateApiRepository(RestTemplate alumnosRestTemplate, AlumnoApiBaseUrl baseUrl) {
        this.restTemplate = alumnosRestTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Alumno> findAll() {
        return restTemplate.exchange(baseUrl.alumnos() + "/", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Alumno>>() {}).getBody();
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        try {
            return Optional.ofNullable(
                    restTemplate.getForObject(baseUrl.alumnos() + "/{id}", Alumno.class, id));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        return restTemplate.postForObject(baseUrl.alumnos() + "/", alumno, Alumno.class);
    }

    @Override
    public Optional<Alumno> update(Long id, AlumnoDTO alumno) {
        // El API devuelve el alumno modificado (o 404 si no existe).
        try {
            return Optional.ofNullable(restTemplate.exchange(baseUrl.alumnos() + "/{id}",
                    HttpMethod.PUT, new HttpEntity<>(alumno), Alumno.class, id).getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Alumno> patch(Long id, JsonNode patch) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json");
        try {
            return Optional.ofNullable(restTemplate.exchange(baseUrl.alumnos() + "/{id}",
                    HttpMethod.PATCH, new HttpEntity<>(patch, headers), Alumno.class, id).getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Alumno> delete(Long id) {
        // El API devuelve el alumno borrado (o 404 si no existe).
        try {
            return Optional.ofNullable(restTemplate.exchange(baseUrl.alumnos() + "/{id}",
                    HttpMethod.DELETE, null, Alumno.class, id).getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
