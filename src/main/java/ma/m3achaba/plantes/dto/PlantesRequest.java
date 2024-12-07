package ma.m3achaba.plantes.dto;

import jakarta.validation.constraints.NotBlank;
import ma.m3achaba.plantes.validation.OnCreate;

public record PlantesRequest(
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")
        String nom
) {
}
