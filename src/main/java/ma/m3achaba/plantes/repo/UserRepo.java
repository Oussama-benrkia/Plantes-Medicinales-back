package ma.m3achaba.plantes.repo;

import ma.m3achaba.plantes.model.Role;
import ma.m3achaba.plantes.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String prenom,String email);
    Page<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String prenom,String email, Pageable pageable);
    Page<User> findByRole(String role, Pageable pageable);
    Optional<User> findByEmail(String email);
    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
            String nom, String prenom, String email, Role role);

    Page<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
            String nom, String prenom, String email, Role role, Pageable pageable);


}
