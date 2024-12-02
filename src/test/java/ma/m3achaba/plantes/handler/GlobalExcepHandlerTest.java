package ma.m3achaba.plantes.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExcepHandlerTest {

    @Test
    void handleException_ShouldReturnInternalServerError_WhenExceptionIsThrown() {
        // Arrange
        GlobalExcepHandler handler = new GlobalExcepHandler();
        Exception exception = new Exception("Simulated exception");

        // Act
        ResponseEntity<ExceptionResponse> response = handler.handleException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.getCode());
        assertEquals("Simulated exception", body.getMessage());
        assertEquals("Erreur interne, veuillez contacter l'administrateur", body.getDetails());
        assertNotNull(body.getDate());
        assertTrue(body.getDate().isBefore(LocalDateTime.now()) || body.getDate().isEqual(LocalDateTime.now()));
    }
}