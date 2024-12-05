package ma.m3achaba.plantes.mapper;

import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.model.Maladies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MaladiesMapperTest {
    MaladiesMapper maladiesMapper;
    Maladies maladies;
    MaladiesResponse maladiesResponse;
    MaladiesRequest maladiesRequest;
    @BeforeEach
    void setUp() {
        maladiesMapper = new MaladiesMapper();
        maladies=Maladies.builder()
                .name("maladies_test")
                .id(1L)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        maladiesResponse=MaladiesResponse.builder()
                .dateUpdated("3/12/2024")
                .dateCreated("3/12/2024")
                .name("maladies_test")
                .id(1L)
                .build();
        maladiesRequest=new MaladiesRequest("maladies_test");
    }

    @Test
    void toEntity() {
        Maladies maladiesRes=maladiesMapper.toEntity(maladiesRequest);
        assertNotNull(maladiesRes);
        assertEquals(maladiesRequest.nom(),maladiesRes.getName());
    }

    @Test
    void toResponse() {
        MaladiesResponse maladiesRequestRes=maladiesMapper.toResponse(maladies);
        assertNotNull(maladiesRequestRes);
        assertEquals(maladies.getName(),maladiesRequestRes.getName());
        assertEquals(maladies.getId(),maladiesRequestRes.getId());
    }
}