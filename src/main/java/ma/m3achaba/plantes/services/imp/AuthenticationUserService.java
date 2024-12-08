package ma.m3achaba.plantes.services.imp;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.RegisterResponse;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo user_repo;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse response = new RegisterResponse();
        try {
            // Vérifier si l'email existe déjà
            if (user_repo.existsByEmail(registerRequest.getEmail())) {
                throw new EntityNotFoundException("User with email " + registerRequest.getEmail() + " already exists");
            }
            // Créer un nouvel utilisateur
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.valueOf(registerRequest.getRole()));
            user.setNom(registerRequest.getNom());
            user.setPrenom(registerRequest.getPrenom());
            // Sauvegarder l'utilisateur
            User newUser = user_repo.save(user);
            // Vérifier si l'utilisateur a été correctement sauvegardé
            if (newUser != null && newUser.getId() > 0) {
                response.setMessage("User registered successfully");
                response.setStatusCode(200);
                response.setEmail(newUser.getEmail());
                response.setRole(String.valueOf(newUser.getRole()));
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    public RegisterResponse login(RegisterRequest loginRequest) {
        RegisterResponse response = new RegisterResponse();
        try {
            User user = user_repo.findByEmail(loginRequest.getEmail());
            if (user != null) {
                // Authentifier l'utilisateur avec l'email et le mot de passe
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getEmail(),
                                loginRequest.getPassword()
                        )
                );
                // Générer les tokens JWT
                String jwt = jwtService.generateToken(user);
                String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
                // Réponse en cas de succès
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshToken);
                response.setExpirationTime("24Hrs");
                response.setMessage("Successfully Logged In");
            } else {
                // Réponse si l'utilisateur n'est pas trouvé
                response.setStatusCode(404);
                response.setMessage("User not found");
            }
        } catch (Exception e) {
            // Gestion des erreurs
            response.setStatusCode(500);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }
    public RegisterResponse refreshToken(RegisterRequest refreshTokenRequest) {
        RegisterResponse response = new RegisterResponse();
        try {
            // Extraire l'email à partir du token
            String ourEmail = jwtService.extractUsername(refreshTokenRequest.getToken());
            // Vérifier si le token est valide
            if (jwtService.isTokenValid(refreshTokenRequest.getToken(), ourEmail)) {
                // Trouver l'utilisateur par l'email extrait du token
                User user = user_repo.findByEmail(ourEmail);
                if (user != null) {
                    // Générer un nouveau token JWT
                    String newJwt = jwtService.generateToken(user);
                    response.setStatusCode(200);
                    response.setToken(newJwt);
                    response.setRefreshToken(refreshTokenRequest.getToken()); // refresh token remains the same
                    response.setExpirationTime("24Hrs");
                    response.setMessage("Successfully Refreshed Token");

                } else {
                    response.setStatusCode(404);
                    response.setMessage("User not found");
                }
            } else {
                response.setStatusCode(400);
                response.setMessage("Invalid token");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An error occurred while refreshing the token: " + e.getMessage());
        }
        return response;
    }



}






