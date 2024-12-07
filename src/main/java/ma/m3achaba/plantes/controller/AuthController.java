package ma.m3achaba.plantes.controller;



import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.services.imp.AuthenticationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationUserService authservice;

    @PostMapping("/auth/register")
    public RegisterRequest register(@RequestBody RegisterRequest registerRequest) {
        return authservice.register(registerRequest);
    }
    @PostMapping("/auth/login")
    public RegisterRequest login(@RequestBody RegisterRequest loginRequest) {
        return authservice.login(loginRequest);
    }
    @PostMapping("/auth/refresh")
    public RegisterRequest refreshToken(@RequestBody RegisterRequest refreshTokenRequest) {
        return authservice.refreshToken(refreshTokenRequest);
    }
    @GetMapping("/admin/users")
    public List<User> findAll() {
        return authservice.findAll();
    }
    @PostMapping("/admin/updateUser/{id}")
    public RegisterRequest updateUser(@PathVariable Long id, @RequestBody RegisterRequest request) {
        request.setId(id);
        return authservice.updateUser(request);
    }
    @DeleteMapping("/admin/deleteUser/{id}")
    public void deleteUser(@PathVariable Long id) {
        authservice.deleteUser(id);
    }
    @GetMapping("user/profile")
    public ResponseEntity<RegisterRequest> getProfile() {
        try {
            RegisterRequest profile = authservice.getProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RegisterRequest.builder()
                            .statusCode(401)
                            .error(e.getMessage())
                            .build());
        }
    }
    @PutMapping("user/updateProfile")
    public ResponseEntity<RegisterRequest> updateProfile(@RequestBody RegisterRequest request) {
        try {
            RegisterRequest response = authservice.updateProfile(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegisterRequest.builder()
                            .statusCode(400)
                            .error(e.getMessage())
                            .build());
        }
    }



    }




