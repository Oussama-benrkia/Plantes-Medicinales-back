package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.RegisterResponse;
import ma.m3achaba.plantes.services.imp.AuthenticationUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;




@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationUserService authservice;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Validated @RequestBody RegisterRequest.RegisterRequestRegister registerRequest) {
        RegisterResponse registerResponse = authservice.register(registerRequest);
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> login(@Validated @RequestBody RegisterRequest.RegisterRequestLogin loginRequest) {
        RegisterResponse loginResponse = authservice.login(loginRequest);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<RegisterResponse> refreshToken(@Validated @RequestBody RegisterRequest.RegisterToken refreshTokenRequest) {
        RegisterResponse refreshResponse = authservice.refreshToken(refreshTokenRequest);
        if (refreshResponse != null) {
            return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

