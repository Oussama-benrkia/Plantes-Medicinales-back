package ma.m3achaba.plantes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Plantes  extends BaseEntity {

    private String name;
    private String description;
    private String utilisation;
    private String region;
    private String precautions;
    private String images;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Maladies-Plantes",
            joinColumns = @JoinColumn(name = "plante_id"),
            inverseJoinColumns = @JoinColumn(name = "maladie_id")
    )
    private List<Maladies> maladies;









}
