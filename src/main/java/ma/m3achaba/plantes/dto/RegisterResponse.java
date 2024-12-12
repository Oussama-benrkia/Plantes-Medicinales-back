package ma.m3achaba.plantes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.m3achaba.plantes.validation.OnCreate;


public class RegisterRequest {
    public static record RegisterRequestLogin(
            @NotBlank(message = "Email must not be blank")
            @Email(message = "Email must be a valid email address")
            String email,

            @NotBlank(message = "Password must not be blank")
            @Size(min = 8, message = "Password must be at least 8 characters long")
            String password
    ){

    }
    public static record RegisterRequestRegister(
            @NotBlank(message = "Prenom must not be blank")
            String prenom,
            @NotBlank(message = "Nom must not be blank")
            String nom,
            @NotBlank(message = "Email must not be blank")
            @Email(message = "Email must be a valid email address")
            String email,
            @NotBlank(message = "Password must not be blank")
            @Size(min = 8, message = "Password must be at least 8 characters long")
            String password
    ){

    }
}
