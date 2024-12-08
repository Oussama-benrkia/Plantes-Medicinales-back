package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.exception.ResourceNotFoundException;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.services.imp.UserService;
import ma.m3achaba.plantes.validation.OnCreate;
import ma.m3achaba.plantes.validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/admin/user/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    @GetMapping("/admin/usersPage")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        PageResponse<UserResponse> response = (search != null && !search.isEmpty())
                ? userService.findAllWithSearch(page, size, search)  // Recherche avec pagination
                : userService.findAll(page, size);                    // Pagination sans recherche
        return ResponseEntity.ok(response);
    }
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> findAllUsers(
            @RequestParam(required = false) String search
    ) {
        List<UserResponse> responses = (search != null && !search.isEmpty())
                ? userService.findAllWithSearch(search)
                : userService.findAll();
        return ResponseEntity.ok(responses);
    }
    @PostMapping("/admin/addUser")
    public UserResponse saveUser(
            @Validated(OnCreate.class) @RequestBody UserRequest request
    ) {
        return userService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to save User"));
    }

    @PutMapping("/admin/updateUser{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody UserRequest request
    ) {
        return userService.update(request, id)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to update User with id: " + id));
    }

    @DeleteMapping("admin/deleteUser/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/user/current")
    public ResponseEntity<User> getCurrentUser() {
        try {
            User userResponse = userService.getCurrentUser();
            return ResponseEntity.ok(userResponse);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/user/profile")
    public ResponseEntity<RegisterRequest> getProfile() {
        try {
            RegisterRequest profile = userService.getProfile();
            return ResponseEntity.ok(profile);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/user/updateProfile")
    public ResponseEntity<RegisterRequest> updateProfile(
            @Validated(OnUpdate.class) @RequestBody RegisterRequest request) {
        try {
            RegisterRequest updatedProfile = userService.updateProfile(request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegisterRequest.builder()
                            .message("Erreur de mise à jour du profil : " + e.getMessage())
                            .statusCode(400)
                            .build());
        }
    }
}
