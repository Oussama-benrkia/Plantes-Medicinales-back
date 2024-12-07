package ma.m3achaba.plantes.services.imp;


import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.RegisterRequest;
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
import java.util.List;
import java.util.Optional;

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
            user.setRole(registerRequest.getRole());
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
                response.setRole(newUser.getRole());
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
            response.setRole(user.getRole());  // Use user.getRole() directly
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

    public List<User> findAll() {
        return user_repo.findAll();
    }

    public RegisterRequest updateUser(RegisterRequest request) {
        RegisterRequest response = new RegisterRequest();
        try {
            // Rechercher l'utilisateur par ID
            Optional<User> existingUser = user_repo.findById(request.getId());
            if (existingUser.isEmpty()) {
                response.setStatusCode(404);
                response.setError("User not found");
                return response;
            }

            User user = existingUser.get();
            // Mise à jour des informations
            user.setNom(request.getNom());
            user.setPrenom(request.getPrenom());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                if (!user.getPassword().equals(request.getPassword())) {
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                }
            }
            user.setRole(request.getRole());
            User updatedUser = user_repo.save(user);

            response.setUsers(updatedUser);
            response.setMessage("User updated successfully");
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public void deleteUser(Long id) {
        user_repo.deleteById(id);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié trouvé.");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("Principal n'est pas une instance de UserDetails.");
        }
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();

        // Rechercher l'utilisateur dans la base de données
        User user = user_repo.findByEmail(username);
        if (user == null) {
            throw new IllegalStateException("Utilisateur non trouvé avec l'email : " + username);
        }
        return user;
    }

    public RegisterRequest getProfile() {
        User currentUser = getCurrentUser();
        return RegisterRequest.builder()
                .id(currentUser.getId())
                .nom(currentUser.getNom())
                .prenom(currentUser.getPrenom())
                .email(currentUser.getEmail())
                .role(currentUser.getRole())
                .build();
    }
    public RegisterRequest updateProfile(RegisterRequest request) {
        User currentUser = getCurrentUser();

        // Mise à jour des informations
        if (request.getNom() != null) currentUser.setNom(request.getNom());
        if (request.getPrenom() != null) currentUser.setPrenom(request.getPrenom());
        if (request.getEmail() != null) currentUser.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Sauvegarder les modifications
        User updatedUser = user_repo.save(currentUser);

        // Construire la réponse
        return RegisterRequest.builder()
                .id(updatedUser.getId())
                .nom(updatedUser.getNom())
                .prenom(updatedUser.getPrenom())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .message("Profil mis à jour avec succès.")
                .statusCode(200)
                .build();
    }
}






