package ma.m3achaba.plantes.mapper;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.dto.UserRequest;
import ma.m3achaba.plantes.dto.UserResponse;
import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserResponse, UserRequest> {

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
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
