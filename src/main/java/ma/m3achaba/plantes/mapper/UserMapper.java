package ma.m3achaba.plantes.mapper;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.model.User;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserResponse, UserRequest> {
    private final Environment environment;

    @Override
    public User toEntity(final UserRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        return User.builder()
                .nom(userRequest.nom())
                .prenom(userRequest.prenom())
                .email(userRequest.email())
                .build();
    }

    @Override
    public UserResponse toResponse(final User user) {
        String serverAddress = environment.getProperty("server.address", "localhost");
        String serverPort = environment.getProperty("server.port", "8080");

        String imageUrl = null;
        if (user != null && user.getImage() != null && !user.getImage().isEmpty()) {
            imageUrl = "http://" + serverAddress + ":" + serverPort + "/api/image/" + user.getImage(); // Assuming image path is in /images
        }
        return user == null ? null : UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .image(imageUrl)  // Only set the image URL if available
                .build();
    }
}