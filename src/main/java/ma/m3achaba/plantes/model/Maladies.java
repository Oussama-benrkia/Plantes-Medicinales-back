package ma.m3achaba.plantes.model;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Maladies extends BaseEntity {
    private String name;


}
