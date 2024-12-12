package ma.m3achaba.plantes.services.imp;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.RegisterResponse;
import ma.m3achaba.plantes.exception.OperationNotPermittedException;
import ma.m3achaba.plantes.mapper.RegisterMapper;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;
    private final RegisterMapper registerMapper;

    /**
     * Registers a new user.
     */
    public RegisterResponse register(RegisterRequest.RegisterRequestRegister registerRequest) {
        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            throw new EntityNotFoundException("User with this email already exists.");
        }

        User newUser = registerMapper.toEntity(registerRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(Role.USER);
        userRepo.save(newUser);

        String jwt = jwtService.generateToken(newUser);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), newUser);

        return RegisterResponse.builder()
                .statusCode(200)
                .message("User registered successfully")
                .token(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticates a user with their email and password.
     */
    public RegisterResponse login(RegisterRequest.RegisterRequestLogin loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with this email."));

        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return RegisterResponse.builder()
                .statusCode(200)
                .message("Login successful")
                .token(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Refreshes a user's JWT token.
     */
    public RegisterResponse refreshToken(RegisterRequest.RegisterToken refreshTokenRequest) {
        String email = jwtService.extractUsername(refreshTokenRequest.getToken());
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            throw new OperationNotPermittedException("Invalid token.");
        }

        String newJwt = jwtService.generateToken(user);
        return RegisterResponse.builder()
                .statusCode(200)
                .message("Token refreshed successfully")
                .token(newJwt)
                .refreshToken(refreshTokenRequest.getToken())
                .build();
    }
}
