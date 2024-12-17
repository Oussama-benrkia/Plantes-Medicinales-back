package ma.m3achaba.plantes.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;
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









}
