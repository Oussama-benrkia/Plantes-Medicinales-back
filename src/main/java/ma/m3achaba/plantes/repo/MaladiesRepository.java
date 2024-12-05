package ma.m3achaba.plantes.repo;
import ma.m3achaba.plantes.model.Maladies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaladiesRepository extends JpaRepository<Maladies, Long> {
    Page<Maladies> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Maladies> findAllByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}
