package com.inetum.demo.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDtoTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ErrorResponseDto.ErrorResponseDtoBuilder completo() {
        return ErrorResponseDto.builder()
                .type("https://inetum.com/errors/validation-error")
                .title("Error de validación")
                .status(400)
                .detail("Datos no válidos")
                .instance("abc-123")
                .timestamp(Instant.parse("2026-09-30T10:15:30Z"))
                .errors(Map.of("email", "formato inválido"));
    }

    @Test
    void builderAssignsAllFields() {
        ErrorResponseDto dto = completo().build();

        assertEquals("https://inetum.com/errors/validation-error", dto.getType());
        assertEquals("Error de validación", dto.getTitle());
        assertEquals(400, dto.getStatus());
        assertEquals("Datos no válidos", dto.getDetail());
        assertEquals("abc-123", dto.getInstance());
        assertEquals(Instant.parse("2026-09-30T10:15:30Z"), dto.getTimestamp());
        assertEquals(Map.of("email", "formato inválido"), dto.getErrors());
    }

    @Test
    void settersModifyFields() {
        ErrorResponseDto dto = ErrorResponseDto.builder().build();
        dto.setStatus(500);
        dto.setTitle("Error interno");

        assertEquals(500, dto.getStatus());
        assertEquals("Error interno", dto.getTitle());
    }

    @Test
    void equalsHashCodeAndToStringFollowContent() {
        ErrorResponseDto a = completo().build();
        ErrorResponseDto b = completo().build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, completo().status(404).build());
        assertTrue(a.toString().contains("abc-123"));
    }

    @Test
    void serializesTimestampAsIsoAndAllFieldsPresent() throws Exception {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(completo().build()));

        assertEquals(400, json.get("status").asInt());
        assertEquals("2026-09-30T10:15:30Z", json.get("timestamp").asText());
        assertEquals("formato inválido", json.get("errors").get("email").asText());
    }

    @Test
    void nullFieldsAreOmittedFromJson() throws Exception {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .status(404).title("No encontrado").build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));

        assertEquals(404, json.get("status").asInt());
        assertFalse(json.has("errors"));
        assertFalse(json.has("detail"));
        assertFalse(json.has("instance"));
        assertFalse(json.has("timestamp"));
    }
}
