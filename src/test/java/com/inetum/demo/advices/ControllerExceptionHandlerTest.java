package com.inetum.demo.advices;

import com.inetum.demo.dtos.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();

    @Test
    void unexpectedExceptionReturns500WithoutLeakingDetails() {
        ResponseEntity<ErrorResponseDto> r = handler.handleUnexpected(
                new IllegalStateException("secreto interno"), new MockHttpServletRequest());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals(500, r.getBody().getStatus());
        assertFalse(r.getBody().getDetail().contains("secreto"));
        assertNull(r.getBody().getErrors());
        assertNotNull(r.getBody().getTimestamp());
    }

    @Test
    void springErrorResponseKeepsItsStatus() {
        ResponseEntity<ErrorResponseDto> r = handler.handleUnexpected(
                new HttpRequestMethodNotSupportedException("PATCH"), new MockHttpServletRequest());

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, r.getStatusCode());
        assertEquals("Method Not Allowed", r.getBody().getTitle());
    }

    @Test
    void springErrorResponseWithNonStandardStatusFallsBackToGenericTitle() {
        ResponseEntity<ErrorResponseDto> r = handler.handleUnexpected(
                new ResponseStatusException(599, "raro", null), new MockHttpServletRequest());

        assertEquals(599, r.getBody().getStatus());
        assertEquals("Error", r.getBody().getTitle());
    }

    @Test
    void malformedBodyReturns400() {
        ResponseEntity<ErrorResponseDto> r = handler.handleNotReadable(
                new HttpMessageNotReadableException("json roto", mock(HttpInputMessage.class)),
                new MockHttpServletRequest());

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Petición mal formada", r.getBody().getTitle());
    }

    @Test
    void requestIdHeaderIsUsedAsInstance() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Request-Id", "abc-123");

        assertEquals("abc-123", handler.handleNotReadable(
                new HttpMessageNotReadableException("x", mock(HttpInputMessage.class)), req)
                .getBody().getInstance());
    }

    @Test
    void blankRequestIdGeneratesUuid() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Request-Id", "  ");

        String instance = handler.handleNotReadable(
                new HttpMessageNotReadableException("x", mock(HttpInputMessage.class)), req)
                .getBody().getInstance();
        assertDoesNotThrow(() -> java.util.UUID.fromString(instance));
    }
}
