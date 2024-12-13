package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.services.imp.UserService;
import ma.m3achaba.plantes.validation.OnUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compte")
public class CompteController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getProfile() {
        return userService.getProfile()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfile(
            @Validated(OnUpdate.class) @RequestBody UserRequest request) {
        return userService.updateProfile(request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
