package ma.m3achaba.plantes.dto;

import jakarta.validation.constraints.NotBlank;
import ma.m3achaba.plantes.validation.OnCreate;

public record PlantesRequest(
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")

        String name,
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")
        String description,
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")
        String utilisation,
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")
        String region,
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")
        String precautions,
        String images
) {
}
