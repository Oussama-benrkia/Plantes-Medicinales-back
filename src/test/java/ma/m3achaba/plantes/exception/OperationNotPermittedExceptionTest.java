package ma.m3achaba.plantes.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationNotPermittedExceptionTest {
    @Test
    void testDefaultConstructor() {
        OperationNotPermittedException exception = new OperationNotPermittedException();
        assertNotNull(exception);
    }

    @Test
    void testMessageConstructor() {
        String errorMessage = "Operation is not permitted";
        OperationNotPermittedException exception = new OperationNotPermittedException(errorMessage);

        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }
}