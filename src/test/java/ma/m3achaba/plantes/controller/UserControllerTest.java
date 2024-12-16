    package ma.m3achaba.plantes.controller;

    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import ma.m3achaba.plantes.dto.UserRequest;
    import ma.m3achaba.plantes.dto.UserResponse;
    import ma.m3achaba.plantes.services.imp.UserService;
    import ma.m3achaba.plantes.exception.ResourceNotFoundException;
    import ma.m3achaba.plantes.common.PageResponse;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
    import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
    import org.springframework.http.MediaType;
    import org.springframework.mock.web.MockMultipartFile;
    import org.springframework.test.context.bean.override.mockito.MockitoBean;
    import org.springframework.test.web.servlet.MockMvc;

    import java.util.Arrays;
    import java.util.List;
    import java.util.Optional;
    @WebMvcTest(controllers = UserController.class)
    @AutoConfigureMockMvc(addFilters = false)
    @ExtendWith(MockitoExtension.class)
    public class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UserService userService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserResponse mockResponse;
        private UserRequest mockRequest;
        private List<UserResponse> mockResponses;  // Added

        @BeforeEach
        void setUp() {
            mockResponse = UserResponse.builder()
                    .id(1L)
                    .nom("Test Nom")
                    .prenom("Test Prenom")
                    .email("test@example.com")
                    .role("USER")
                    .build();

            mockRequest = new UserRequest("Test Prenom", "Test Nom", "test@example.com", "password123", "USER", null);

            // Initialize mockResponses
            mockResponses = Arrays.asList(mockResponse);
        }

        @Test
        void findById_ExistingId_ReturnsOk() throws Exception {
            when(userService.findById(1L)).thenReturn(Optional.of(mockResponse));

            mockMvc.perform(get("/api/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nom").value("Test Nom"))
                    .andExpect(jsonPath("$.prenom").value("Test Prenom"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        void findById_NonExistingId_ThrowsResourceNotFoundException() throws Exception {
            when(userService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/user/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void findAll_WithoutSearch_ReturnsPageResponse() throws Exception {
            PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                    .content(Arrays.asList(mockResponse))
                    .build();

            when(userService.findAll(0, 10)).thenReturn(pageResponse);

            mockMvc.perform(get("/api/user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nom").value("Test Nom"));
        }

        @Test
        void findAll_WithoutSearch_AndWithRole_ReturnsPageResponse() throws Exception {
            PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                    .content(Arrays.asList(mockResponse))
                    .build();

            when(userService.findAllWithRole(0, 10, "USER")).thenReturn(pageResponse);

            mockMvc.perform(get("/api/user?role=USER"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].role").value("USER"));
        }

        @Test
        void saveUser_ValidRequest_ReturnsCreated() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", // Parameter name in UserRequest
                    "test.jpg", // File name
                    MediaType.IMAGE_JPEG_VALUE, // Content type
                    "dummy-image-content".getBytes() // Content
            );

            // Mocking service response
            when(userService.save(any(UserRequest.class)))
                    .thenReturn(Optional.of(mockResponse)); // Ensure mockResponse is properly set up

            mockMvc.perform(multipart("/api/user")
                            .file(file)
                            .param("prenom", "Test Prenom")
                            .param("nom", "Test Nom")
                            .param("email", "test@example.com")
                            .param("password", "password12345")
                            .param("role", "USER"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nom").value("Test Nom"));
        }

        @Test
        void saveUser_InvalidRequest_ThrowsException() throws Exception {
            mockRequest = new UserRequest("", "", "", "", "USER", null); // Invalid payload

            mockMvc.perform(post("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest)))
                    .andExpect(status().isBadRequest()) // Change expected status to 400
                    .andExpect(jsonPath("$.message").value("Validation Failed"));
        }

        @Test
        void updateUser_ValidRequest_ReturnsOk() throws Exception {
            when(userService.update(any(UserRequest.class), eq(1L))).thenReturn(Optional.of(mockResponse));

            mockMvc.perform(put("/api/user/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.nom").value("Test Nom"))
                    .andExpect(jsonPath("$.prenom").value("Test Prenom"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        void updateUser_InvalidRequest_ThrowsException() throws Exception {
            when(userService.update(any(UserRequest.class), eq(99L))).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/user/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(mockRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void findAllUsers_NoFilters_ReturnsAllUsers() throws Exception {
            when(userService.findAll()).thenReturn(mockResponses);

            mockMvc.perform(get("/api/user/list"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(mockResponses.size()));
        }

        @Test
        void findAllUsers_FilterByRole_ReturnsFilteredUsers() throws Exception {
            String role = "USER";
            when(userService.findAllWithRole(role)).thenReturn(mockResponses);

            mockMvc.perform(get("/api/user/list").param("role", role))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(mockResponses.size()));
        }

        @Test
        void findAllUsers_FilterBySearch_ReturnsFilteredUsers() throws Exception {
            String search = "Test";
            when(userService.findAllWithSearchAndRole(search, "")).thenReturn(mockResponses);

            mockMvc.perform(get("/api/user/list").param("search", search))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(mockResponses.size()));
        }

        @Test
        void findAllUsers_FilterBySearchAndRole_ReturnsFilteredUsers() throws Exception {
            String search = "Test";
            String role = "USER";
            when(userService.findAllWithSearchAndRole(search, role)).thenReturn(mockResponses);

            mockMvc.perform(get("/api/user/list")
                            .param("search", search)
                            .param("role", role))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(mockResponses.size()));
        }
        @Test
        void findAllUserspage_FilterBySearchAndRole_ReturnsFilteredUsers() throws Exception {
            String search = "Test";
            String role = "USER";
            PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                    .content(Arrays.asList(mockResponse))
                    .build();
            when(userService.findAllWithSearchAndRole(0,10,search, role)).thenReturn(pageResponse);

            mockMvc.perform(get("/api/user")
                            .param("search", search)
                            .param("role", role))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nom").value("Test Nom"));
        }
        @Test
        void deleteUser_ExistingId_DeletesResource() throws Exception {
            // Arrange: Mocking the service layer response for deletion
            when(userService.delete(1L)).thenReturn(Optional.of(mockResponse));

            // Act & Assert: Perform the delete request and expect a NO_CONTENT status
            mockMvc.perform(delete("/api/user/1"))
                    .andExpect(status().isNoContent()); // Expecting HTTP 204 No Content status
        }

    }
