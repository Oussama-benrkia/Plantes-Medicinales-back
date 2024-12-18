package ma.m3achaba.plantes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Maladies extends BaseEntity {
    private String name;
    @ManyToMany(mappedBy = "maladies")
    private List<Plantes> plantes;
}
