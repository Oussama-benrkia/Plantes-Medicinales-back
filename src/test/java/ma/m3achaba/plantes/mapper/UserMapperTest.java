package ma.m3achaba.plantes.mapper;

import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) // Add this annotation to enable Mockito in JUnit 5
class UserMapperTest {
    @Mock
    private Environment environment;

    @InjectMocks
    private UserMapper userMapper;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // Initialize the test data
        userRequest = new UserRequest("John", "Doe", "john.doe@example.com", "password123", "USER", null);
        user = new User("John", "Doe", "john.doe@example.com", "password123", Role.USER, null);

        // Use lenient stubbing to avoid unnecessary stubbing exceptions
        lenient().when(environment.getProperty("server.address", "localhost")).thenReturn("localhost");
        lenient().when(environment.getProperty("server.port", "8080")).thenReturn("8080");
    }


    @Test
    void testToEntity() {
        User entity = userMapper.toEntity(userRequest);

        assertNotNull(entity);
        assertEquals(userRequest.nom(), entity.getNom());
        assertEquals(userRequest.prenom(), entity.getPrenom());
        assertEquals(userRequest.email(), entity.getEmail());
        assertNull(entity.getImage()); // Assuming image is not set in the request
    }
    @Test
    void testToEntitynull() {
        User entity = userMapper.toEntity(null);

        assertNull(entity);
    }

    @Test
    void testToResponse() {
        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getNom(), response.getNom());
        assertEquals(user.getPrenom(), response.getPrenom());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(String.valueOf(user.getRole()), response.getRole());
        assertNull(response.getImage()); // Image should be null if it's not set on user
    }

    @Test
    void testToResponseWithImage() {
        // Update the user to include an image
        user.setImage("profile.jpg");
        UserResponse response = userMapper.toResponse(user);

        assertNotNull(response);
        assertEquals("http://localhost:8080/api/image/profile.jpg", response.getImage());
    }

    @Test
    void testToResponseWithNullUser() {
        UserResponse response = userMapper.toResponse(null);
        assertNull(response);
    }
}
