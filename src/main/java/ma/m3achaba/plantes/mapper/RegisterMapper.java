package ma.m3achaba.plantes.mapper;

import ma.m3achaba.plantes.dto.RegisterRequest;
import ma.m3achaba.plantes.model.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterMapper {
    public User toEntity(final RegisterRequest.RegisterRequestRegister userRequest) {
        if (userRequest == null) {
            return null;
        }
        return User.builder()
                .nom(userRequest.getNom())
                .prenom(userRequest.getPrenom())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .build();
    }
}
