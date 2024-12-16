package ma.m3achaba.plantes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;


@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity {

    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String image;


}
