package ma.m3achaba.plantes.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.persistence.EntityNotFoundException;
import ma.m3achaba.plantes.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

class GlobalExcepHandlerTest {

    private GlobalExcepHandler globalExcepHandler;

    @BeforeEach
    void setUp() {
        globalExcepHandler = new GlobalExcepHandler();
    }

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExcepHandler.handleResourceNotFound(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Resource not found", body.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getCode());
        assertNotNull(body.getDate());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExcepHandler.handleEntityNotFoundException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Entity not found", body.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getCode());
        assertNotNull(body.getDate());
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "Invalid input");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<ExceptionResponse> response = globalExcepHandler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getCode());
        assertEquals("Validation Failed", body.getMessage());
        assertNotNull(body.getDate());

        // Check field errors
        Map<String, String> errors = body.getErros();
        assertNotNull(errors);
        assertTrue(errors.containsKey("fieldName"));
        assertEquals("Invalid input", errors.get("fieldName"));
    }

    @Test
    void testDateInExceptionResponse_ShouldBeCurrentOrPastTime() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Test exception");

        // Act
        ResponseEntity<ExceptionResponse> response = globalExcepHandler.handleResourceNotFound(ex);

        // Assert
        ExceptionResponse body = response.getBody();
        assertNotNull(body);
        LocalDateTime responseDate = body.getDate();

        assertTrue(responseDate.isBefore(LocalDateTime.now()) ||
                        responseDate.isEqual(LocalDateTime.now()),
                "Response date should be current or in the past");
    }
}