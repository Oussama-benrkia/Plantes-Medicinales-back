package ma.m3achaba.plantes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaladiesResponse {
    private Long id;
    private String name;
    private String dateCreated;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String dateUpdated;
}
