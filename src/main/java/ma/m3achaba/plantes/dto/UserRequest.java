package ma.m3achaba.plantes.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ma.m3achaba.plantes.validation.OnCreate;
import ma.m3achaba.plantes.validation.OnUpdate;
import org.springframework.web.multipart.MultipartFile;

public record UserRequest(
        @NotBlank( groups = OnCreate.class, message = "Prenom must not be blank")
        String prenom,
        @NotBlank(groups = OnCreate.class,message = "Nom must not be blank")
        String nom,
        @NotBlank(groups = OnCreate.class,message = "Email must not be blank")
        @Email(groups = {OnCreate.class, OnUpdate.class},message = "Email must be a valid email address")
        String email,
        @NotBlank(groups = OnCreate.class,message = "Password must not be blank")
        @Size(groups = OnCreate.class,min = 8, message = "Password must be at least 8 characters long")
        String password,
        String role,
        MultipartFile file
) {}