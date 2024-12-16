package ma.m3achaba.plantes.services.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.exception.OperationNotPermittedException;
import ma.m3achaba.plantes.mapper.UserMapper;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.repo.UserRepo;
import ma.m3achaba.plantes.util.images.ImgService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ImgService imgService;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("John", "Doe", "john.doe@example.com", "password123", "USER", null);
        user = new User("John", "Doe", "john.doe@example.com", "1234", Role.USER, null);
    }
    @Test
    void testFindAll() {
        // Simuler une page d'utilisateurs
        Page<User> userPage = mock(Page.class);
        when(userPage.getContent()).thenReturn(List.of(user));
        when(userPage.getTotalElements()).thenReturn(1L);
        when(userPage.getTotalPages()).thenReturn(1);
        when(userPage.getNumber()).thenReturn(0);
        when(userPage.isFirst()).thenReturn(true);
        when(userPage.isLast()).thenReturn(true);

        // Configurer le comportement du UserRepo pour findAll
        when(userRepo.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

        // Configurer le UserMapper pour transformer l'entité User en UserResponse
        UserResponse userResponse = new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Appel de la méthode findAll
        PageResponse<UserResponse> response = userService.findAll(0, 10);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null.");
        assertEquals(1, response.getTotalElements(), "Le nombre total d'éléments doit être égal à 1.");
        assertEquals(1, response.getTotalPages(), "Le nombre total de pages doit être égal à 1.");
        assertTrue(response.isFirst(), "La page doit être la première.");
        assertTrue(response.isLast(), "La page doit être la dernière.");
        assertFalse(response.getContent().isEmpty(), "Le contenu de la page ne doit pas être vide.");
        assertEquals("John", response.getContent().get(0).getNom(), "Le nom du premier utilisateur doit être 'John'.");

        // Vérification des appels
        verify(userRepo).findAll(PageRequest.of(0, 10));
        verify(userMapper).toResponse(user);
    }


    @Test
    void testFindById() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null));

        Optional<UserResponse> response = userService.findById(1L);

        assertTrue(response.isPresent());
        assertEquals("John", response.get().getNom());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<UserResponse> response = userService.findById(1L);

        assertFalse(response.isPresent());
    }

    @Test
    void testSave() {
        when(userRepo.existsByEmail(userRequest.email())).thenReturn(false);
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null));

        Optional<UserResponse> response = userService.save(userRequest);

        assertTrue(response.isPresent());
        assertEquals("John", response.get().getNom());
        verify(userRepo).save(user);
    }

    @Test
    void testSaveEmailAlreadyExists() {
        when(userRepo.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(OperationNotPermittedException.class, () -> userService.save(userRequest));
    }

    @Test
    void testUpdate() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, "John", "Doe", "new.email@example.com", "USER", null));

        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true); // Simuler un fichier vide

        UserRequest updateRequest = new UserRequest("John", "Doe", "new.email@example.com", "newpassword", "USER", mockFile);
        when(userRepo.save(user)).thenReturn(user);

        Optional<UserResponse> response = userService.update(updateRequest, 1L);

        assertTrue(response.isPresent());
        assertEquals("new.email@example.com", response.get().getEmail());
        verify(userRepo).save(user);
    }
    @Test
    void testFindAllWithSearchAndRoleWithoutRole() {
        // Données simulées
        int page = 0;
        int size = 5;
        String search = "John";
        String role = ""; // Pas de rôle
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = new PageImpl<>(List.of(
                new User("John", "Doe", "john.doe@example.com", "1234", Role.USER, ""),
                new User("Johnny", "Smith", "johnny.smith@example.com", "5678", Role.ADMIN, "")
        ));

        // Simulation des dépendances
        when(userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search, pageable)).thenReturn(usersPage);

        // Appel de la méthode
        PageResponse<UserResponse> response = userService.findAllWithSearchAndRole(page, size, search, role);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être nulle.");
        assertEquals(2, response.getContent().size(), "La réponse doit contenir deux utilisateurs.");
        verify(userRepo).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search, pageable);
    }

    @Test
    void testFindAllWithSearchAndRoleWithRole() {
        // Données simulées
        int page = 0;
        int size = 5;
        String search = "Jane";
        String role = "USER"; // Avec rôle
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = new PageImpl<>(List.of(
                new User("Jane", "Smith", "jane.smith@example.com", "5678", Role.USER, "")
        ));

        // Simulation des dépendances
        when(userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
                search, search, search, Role.valueOf(role), pageable)).thenReturn(usersPage);

        // Appel de la méthode
        PageResponse<UserResponse> response = userService.findAllWithSearchAndRole(page, size, search, role);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être nulle.");
        assertEquals(1, response.getContent().size(), "La réponse doit contenir un utilisateur.");
        verify(userRepo).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
                search, search, search, Role.valueOf(role), pageable);
    }
    @Test
    void testFindAllWithRole() {
        // Données simulées
        String role = "USER";
        List<User> users = List.of(
                new User("John", "Doe", "john.doe@example.com", "1234", Role.USER, ""),
                new User("Jane", "Smith", "jane.smith@example.com", "5678", Role.USER, "")
        );
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name(), user.getImage()))
                .toList();

        // Simulation des dépendances
        when(userRepo.findAllByRole(role)).thenReturn(users);
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponse(user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name(), user.getImage());
        });

        // Appel de la méthode
        List<UserResponse> response = userService.findAllWithRole(role);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être nulle.");
        assertEquals(2, response.size(), "La réponse doit contenir deux utilisateurs.");
        assertEquals(userResponses, response, "Les réponses doivent correspondre aux utilisateurs simulés.");
        verify(userRepo).findAllByRole(role);
        verify(userMapper, times(users.size())).toResponse(any(User.class));
    }


    @Test
    void testUpdateUserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        UserRequest updateRequest = new UserRequest("John", "Doe", "new.email@example.com", "newpassword", "USER", null);

        assertThrows(EntityNotFoundException.class, () -> userService.update(updateRequest, 1L));
    }

    @Test
    void testFindAllWithSearchAndRole() {
        when(userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
                "search", "search", "search", Role.USER))
                .thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null));

        List<UserResponse> response = userService.findAllWithSearchAndRole("search", "USER");

        assertFalse(response.isEmpty());
        assertEquals("John", response.get(0).getNom());
    }

    @Test
    void testFindAllUsers() {
        // Simuler une liste d'utilisateurs
        List<User> users = List.of(
                new User("John", "Doe", "john.doe@example.com", "1234", Role.USER, null),
                new User("Jane", "Smith", "jane.smith@example.com", "5678", Role.ADMIN, null)
        );

        // Configurer le comportement du UserRepo
        when(userRepo.findAll()).thenReturn(users);

        // Configurer le UserMapper pour transformer chaque User en UserResponse
        List<UserResponse> userResponses = List.of(
                new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null),
                new UserResponse(2L, "Jane", "Smith", "jane.smith@example.com", "ADMIN", null)
        );
        when(userMapper.toResponse(users.get(0))).thenReturn(userResponses.get(0));
        when(userMapper.toResponse(users.get(1))).thenReturn(userResponses.get(1));

        // Appel de la méthode findAll
        List<UserResponse> response = userService.findAll();

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être null.");
        assertEquals(2, response.size(), "La taille de la liste doit être de 2.");
        assertEquals("John", response.get(0).getNom(), "Le nom du premier utilisateur doit être 'John'.");
        assertEquals("Jane", response.get(1).getNom(), "Le nom du deuxième utilisateur doit être 'Jane'.");

        // Vérification des appels
        verify(userRepo).findAll();
        verify(userMapper).toResponse(users.get(0));
        verify(userMapper).toResponse(users.get(1));
    }
    @Test
    void testDeleteUserWithoutImage() {
        // Simuler les données
        Long userId = 2L;
        User user = new User("Jane", "Smith", "jane.smith@example.com", "5678", Role.ADMIN, "");
        UserResponse userResponse = new UserResponse(2L, "Jane", "Smith", "jane.smith@example.com", "ADMIN", "");

        // Configurer les comportements des dépendances
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).delete(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // Appel de la méthode delete
        Optional<UserResponse> response = userService.delete(userId);

        // Vérifications
        assertTrue(response.isPresent(), "La réponse doit être présente.");
        assertEquals(userResponse, response.get(), "La réponse doit correspondre à l'utilisateur supprimé.");
        assertEquals("", response.get().getImage(), "L'utilisateur ne doit pas avoir d'image.");

        // Vérification des appels
        verify(userRepo).findById(userId);
        verify(userRepo).delete(user);
        verify(imgService, never()).deleteImage(anyString());
        verify(userMapper).toResponse(user);
    }

    @Test
    void testFindAllWithRolePageable() {
        Page<User> page = Mockito.mock(Page.class);
        when(page.getContent()).thenReturn(List.of(user));
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(0);
        when(page.isFirst()).thenReturn(true);
        when(page.isLast()).thenReturn(true);
        when(userRepo.findAllByRole("USER", PageRequest.of(0, 10))).thenReturn(page);
        when(userMapper.toResponse(user)).thenReturn(new UserResponse(1L, "John", "Doe", "john.doe@example.com", "USER", null));

        PageResponse<UserResponse> response = userService.findAllWithRole(0, 10, "USER");

        assertFalse(response.getContent().isEmpty());
        assertEquals("John", response.getContent().get(0).getNom());
    }
    @Test
    void testFindAllWithSearchAndRole_EmptyRole() {
        // Données simulées
        String search = "John";
        String role = ""; // Rôle vide
        List<User> users = List.of(
                new User("John", "Doe", "john.doe@example.com", "1234", Role.USER, ""),
                new User("Johnny", "Smith", "johnny.smith@example.com", "5678", Role.ADMIN, "")
        );

        // Simulation des dépendances
        when(userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search)).thenReturn(users);
        when(userMapper.toResponse(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new UserResponse(user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name(), user.getImage());
        });

        // Appel de la méthode
        List<UserResponse> response = userService.findAllWithSearchAndRole(search, role);

        // Vérifications
        assertNotNull(response, "La réponse ne doit pas être nulle.");
        assertEquals(2, response.size(), "La réponse doit contenir deux utilisateurs.");
        assertEquals("John", response.get(0).getNom(), "Le premier utilisateur doit être John.");
        assertEquals("Johnny", response.get(1).getNom(), "Le deuxième utilisateur doit être Johnny.");

        // Vérification des appels
        verify(userRepo).findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, search);
        verify(userMapper, times(users.size())).toResponse(any(User.class));
    }
    @Test
    void testDeleteUserWithImage() {
        // Prepare test data
        Long userId = 2L;
        String imagePath = "    /user/image.jpg";
        User user = new User("Jane", "Smith", "jane.smith@example.com", "5678", Role.ADMIN, imagePath);
        UserResponse userResponse = new UserResponse(2L, "Jane", "Smith", "jane.smith@example.com", "ADMIN", imagePath);

        // Configure mock behaviors
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepo).delete(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        doNothing().when(imgService).deleteImage(imagePath);

        // Call the delete method
        Optional<UserResponse> response = userService.delete(userId);

        // Assertions
        assertTrue(response.isPresent(), "The response must be present.");
        assertEquals(userResponse, response.get(), "The response must match the deleted user.");
        assertEquals(imagePath, response.get().getImage(), "The user's image path should be preserved in the response.");

        // Verify method invocations
        verify(userRepo).findById(userId);
        verify(userRepo).delete(user);
        verify(imgService).deleteImage(imagePath);
        verify(userMapper).toResponse(user);
    }
}
