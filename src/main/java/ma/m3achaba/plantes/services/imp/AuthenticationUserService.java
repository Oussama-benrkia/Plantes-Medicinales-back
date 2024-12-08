package ma.m3achaba.plantes.services.imp;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo user_repo;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterRequest register(RegisterRequest registerRequest) {
        RegisterRequest response = new RegisterRequest();
        try {
            User user = new User();
            // Assigner les informations reçues dans la requête
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.valueOf(registerRequest.getRole()));
            user.setNom(registerRequest.getNom());  // Assigner le nom
            user.setPrenom(registerRequest.getPrenom());  // Assigner le prénom

            // Sauvegarder l'utilisateur dans la base de données
            User newUser = user_repo.save(user);
            if (newUser != null && newUser.getId() > 0) {
                // Assigner les valeurs dans la réponse
                response.setMessage("User registered successfully");
                response.setStatusCode(200);
                response.setEmail(newUser.getEmail());
                response.setNom(newUser.getNom());  // Récupérer le nom
                response.setPrenom(newUser.getPrenom());  // Récupérer le prénom
                response.setRole(String.valueOf(newUser.getRole()));
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public RegisterRequest login(RegisterRequest loginRequest) {
        RegisterRequest response = new RegisterRequest();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            User user = user_repo.findByEmail(loginRequest.getEmail());
            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(String.valueOf(user.getRole()));  // Use user.getRole() directly
            response.setPrenom(user.getPrenom());
            response.setEmail(user.getEmail());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public RegisterRequest refreshToken(RegisterRequest  refreshTokenRequest){
        RegisterRequest response = new RegisterRequest ();
        try{
            String ourEmail = jwtService.extractUsername(refreshTokenRequest.getToken());
            User users = user_repo.findByEmail(ourEmail);
            if (jwtService.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtService.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


}






