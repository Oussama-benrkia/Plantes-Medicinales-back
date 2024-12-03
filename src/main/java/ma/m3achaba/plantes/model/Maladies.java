package ma.m3achaba.plantes.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.m3achaba.plantes.common.BaseEntity;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Maladies extends BaseEntity {
    private String name;
}
