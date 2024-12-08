package ma.m3achaba.plantes.services.imp;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;

import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.mapper.UserMapper;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import ma.m3achaba.plantes.repo.UserRepo;
import ma.m3achaba.plantes.services.ServiceMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService, ServiceMetier<UserResponse, UserRequest> {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username);
    }
    private User findUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " not found"));
    }
    @Override
    public Optional<UserResponse> findById(Long id) {
        return Optional.ofNullable(userMapper.toResponse(findUserById(id)));
    }

    @Override
    public PageResponse<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> res = userRepo.findAll(pageable);
        return createPageResponse(res);
    }
    private PageResponse<UserResponse> createPageResponse(Page<User> page) {
        List<UserResponse> list = page.getContent().stream()
                .map(userMapper::toResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .totalElements(page.getTotalElements())
                .number(page.getNumber())
                .last(page.isLast())
                .first(page.isFirst())
                .content(list)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .build();
    }
    public PageResponse<UserResponse> findAllWithSearch(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        // Recherche par nom, prénom ou email
        Page<User> users = userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(search, search, pageable);
        return createPageResponse(users);
    }
    public List<UserResponse> findAllWithSearch(String search) {
        // Recherche sans pagination
        List<User> users = userRepo.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(search, search);
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepo.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<UserResponse> save(UserRequest t) {
        if (userRepo.existsByEmail(t.getEmail())) {
            throw new EntityNotFoundException("User with email " + t.getEmail() + " already exists");
        }
        User user = User.builder()
                .nom(t.getNom())
                .prenom(t.getPrenom())
                .email(t.getEmail())
                .role(Role.valueOf(t.getRole()))
                .build();

        // Sauvegarde l'utilisateur et récupère la réponse
        User savedUser = userRepo.save(user);

        // Retourne l'utilisateur sauvegardé en tant que réponse
        return Optional.ofNullable(userMapper.toResponse(savedUser));
    }


    @Override
    public Optional<UserResponse> update(UserRequest t, Long id) {
        User user = findUserById(id);  // Find the user by ID
        boolean change = false;
        // Check if the name is being updated
        if (t.getNom() != null && !t.getNom().isEmpty() && !t.getNom().equals(user.getNom())) {
            user.setNom(t.getNom());
            change = true;
        }
        // Check if the first name is being updated
        if (t.getPrenom() != null && !t.getPrenom().isEmpty() && !t.getPrenom().equals(user.getPrenom())) {
            user.setPrenom(t.getPrenom());
            change = true;
        }
        // Check if the email is being updated
        if (t.getEmail() != null && !t.getEmail().isEmpty() && !t.getEmail().equals(user.getEmail())) {
            user.setEmail(t.getEmail());
            change = true;
        }
        // Check if the role is being updated
        if (t.getRole() != null && !t.getRole().equals(user.getRole().name())) {
            user.setRole(Role.valueOf(t.getRole()));
            change = true;
        }
        // Save the user if any changes have been made
        if (change) {
            user = userRepo.save(user); // Save the updated user
        }
        // Return the updated user response
        return Optional.ofNullable(userMapper.toResponse(user));
    }

    @Override
    public Optional<UserResponse> delete(Long id) {
        // Find the user by ID (will throw exception if not found)
        User user = findUserById(id);
        // Delete the user from the repository
        userRepo.delete(user);
        // Return the response after deletion
        return Optional.ofNullable(userMapper.toResponse(user));
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
        User user = userRepo.findByEmail(username);
        if (user == null) {
            throw new IllegalStateException("Utilisateur non trouvé avec l'email : " + username);
        }
        return user;
    }
    public RegisterRequest getProfile() {
        User currentUser = getCurrentUser();
        return RegisterRequest.builder()
                .nom(currentUser.getNom())
                .prenom(currentUser.getPrenom())
                .email(currentUser.getEmail())
                .role(currentUser.getRole().name()) // Assuming Role is an enum
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
        User updatedUser = userRepo.save(currentUser);

        // Construire la réponse
        return RegisterRequest.builder()
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole().name()) // Assuming Role is an enum
                .message("Profil mis à jour avec succès.")
                .statusCode(200)
                .build();
    }


}
