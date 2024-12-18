package ma.m3achaba.plantes.mapper;

import ma.m3achaba.plantes.dto.PlantesRequest;
import ma.m3achaba.plantes.dto.PlantesResponse;
import ma.m3achaba.plantes.model.Maladies;
import ma.m3achaba.plantes.model.Plantes;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
@Component
public class PlantesMapper implements Mapper<Plantes, PlantesResponse, PlantesRequest>{
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Override
    public Plantes toEntity(final PlantesRequest maladies) {
        return Plantes.builder()
                .name(maladies.name())
                .description(maladies.description())
                .precautions(maladies.precautions())
                .region(maladies.region())
                .utilisation(maladies.utilisation())
                .build();
    }
        @Override
        public PlantesResponse toResponse(final Plantes plantes) {
            return PlantesResponse.builder()
                    .id(plantes.getId())
                    .description(plantes.getDescription())
                    .precautions(plantes.getPrecautions())
                    .region(plantes.getRegion())
                    .utilisation(plantes.getUtilisation())
                    .name(plantes.getName())
                    .maladies(plantes.getMaladies().stream().map(Maladies::getName).toList())
                    .dateCreated(plantes.getCreatedDate().format(formatter))
                    .build();
        }
}
