package ma.m3achaba.plantes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PlantesResponse {
    private Long id;
    private String name;
    private String description;
    private String region;
    private String utilisation;
    private String precautions;
    private String image;
    private String dateCreated;
    private List<String> maladies;
}
