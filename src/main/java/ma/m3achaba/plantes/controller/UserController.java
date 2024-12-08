package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.RegisterResponse;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.exception.ResourceNotFoundException;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.services.imp.UserService;
import ma.m3achaba.plantes.validation.OnCreate;
import ma.m3achaba.plantes.validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // Permet de récupérer un utilisateur par son ID (accessible par ADMIN uniquement)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // Permet de lister les utilisateurs avec pagination et recherche (accessible par ADMIN uniquement)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        // Recherche et pagination
        PageResponse<UserResponse> response = (search != null && !search.isEmpty())
                ? userService.findAllWithSearch(page, size, search)  // Recherche avec pagination
                : userService.findAll(page, size);                    // Pagination sans recherche
        return ResponseEntity.ok(response);
    }

    // Permet de lister tous les utilisateurs sans pagination (accessible par ADMIN uniquement)
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAllUsers(
            @RequestParam(required = false) String search
    ) {
        // Recherche sans pagination
        List<UserResponse> responses = (search != null && !search.isEmpty())
                ? userService.findAllWithSearch(search)  // Recherche sans pagination
                : userService.findAll();                  // Récupère tous les utilisateurs

        return ResponseEntity.ok(responses);
    }

    // Permet à un ADMIN de créer un utilisateur
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse saveUser(
            @Validated(OnCreate.class) @RequestBody UserRequest request
    ) {
        return userService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to save User"));
    }

    // Permet à un ADMIN de mettre à jour un utilisateur par son ID
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody UserRequest request
    ) {
        return userService.update(request, id)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to update User with id: " + id));
    }

    // Permet à un ADMIN de supprimer un utilisateur par son ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    // Permet à un utilisateur connecté d'obtenir ses informations personnelles
    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser() {
        try {
            User userResponse = userService.getCurrentUser();
            return ResponseEntity.ok(userResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Permet à un utilisateur connecté d'obtenir son profil
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RegisterRequest> getProfile() {
        try {
            RegisterRequest profile = userService.getProfile();
            return ResponseEntity.ok(profile);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Permet à un utilisateur connecté de mettre à jour son profil
    @PutMapping("/profile")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RegisterResponse> updateProfile(
            @Validated(OnUpdate.class) @RequestBody RegisterRequest request) {
        try {
            RegisterResponse updatedProfile = userService.updateProfile(request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegisterResponse.builder()
                            .message("Erreur de mise à jour du profil : " + e.getMessage())
                            .statusCode(400)
                            .build());
        }
    }
}
