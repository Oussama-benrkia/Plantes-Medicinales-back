package ma.m3achaba.plantes.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {


        @Test
        void testConstructorWithMessage() {
            // Arrange
            String expectedMessage = "Resource not found";

            // Act
            ResourceNotFoundException exception = new ResourceNotFoundException(expectedMessage);

            // Assert
            assertNotNull(exception);
            assertEquals(expectedMessage, exception.getMessage());
            assertTrue(exception instanceof RuntimeException);
        }

        @Test
        void testExceptionInheritance() {
            // Verify that ResourceNotFoundException extends RuntimeException
            ResourceNotFoundException exception = new ResourceNotFoundException("Test");
            assertTrue(exception instanceof RuntimeException,
                    "ResourceNotFoundException should extend RuntimeException");
        }

        @Test
        void testDifferentMessages() {
            // Verify that different messages can be passed
            String message1 = "First resource not found";
            String message2 = "Another resource missing";

            ResourceNotFoundException exception1 = new ResourceNotFoundException(message1);
            ResourceNotFoundException exception2 = new ResourceNotFoundException(message2);

            assertEquals(message1, exception1.getMessage());
            assertEquals(message2, exception2.getMessage());
        }
}