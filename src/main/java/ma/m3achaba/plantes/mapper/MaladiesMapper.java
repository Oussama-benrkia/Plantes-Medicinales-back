package ma.m3achaba.plantes.mapper;

import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.model.Maladies;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
@Component
public class MaladiesMapper implements Mapper<Maladies,MaladiesResponse,MaladiesRequest> {
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    @Override
    public Maladies toEntity(final MaladiesRequest maladies) {
        return Maladies.builder()
                .name(maladies.nom())
                .build();
    }
    @Override
    public MaladiesResponse toResponse(final Maladies maladies) {
        return MaladiesResponse.builder()
                .id(maladies.getId())
                .name(maladies.getName())
                .dateCreated(maladies.getCreatedDate().format(formatter))
                .build();
    }
}
