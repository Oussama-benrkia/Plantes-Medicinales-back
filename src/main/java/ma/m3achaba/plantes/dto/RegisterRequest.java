package ma.m3achaba.plantes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

public class RegisterRequest {

    @Value
    @Builder
    public static class RegisterRequestLogin {
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email;

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password;
    }

    @Value
    @Builder
    public static class RegisterRequestRegister {
        @NotBlank(message = "Prenom must not be blank")
        String prenom;

        @NotBlank(message = "Nom must not be blank")
        String nom;

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email;

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password;

        MultipartFile file;
    }

    @Value
    @Builder
    public static class RegisterToken {
        @NotBlank(message = "Token must not be blank")
        String token;
    }
}
