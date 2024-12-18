package ma.m3achaba.plantes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ma.m3achaba.plantes.validation.OnCreate;

import java.util.List;

public record PlantesRequest(
        @NotBlank(groups = OnCreate.class, message = "Nom must not be blank")

        String name,

        @NotBlank(groups = OnCreate.class, message = "Description must not be blank")
        String description,

        @NotBlank(groups = OnCreate.class, message = "utilisation must not be blank")
        String utilisation,

        @NotBlank(groups = OnCreate.class, message = "region must not be blank")
        String region,

        @NotBlank(groups = OnCreate.class, message = "precautions must not be blank")
        String precautions,
        @NotNull(groups = OnCreate.class, message = "maladies must not be blank")
        List<Integer> maladie,

        String images
) {
}
