package ma.m3achaba.plantes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.m3achaba.plantes.common.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity implements UserDetails {

    private String nom;
    private String prenom;
    private String email;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;  // Explicitly return the email as the username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Adjust based on business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Adjust based on business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Adjust based on business logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Adjust based on business logic
    }
}
