package ma.m3achaba.plantes.repo;

import ma.m3achaba.plantes.model.Maladies;
import ma.m3achaba.plantes.model.Plantes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantesRepository extends JpaRepository<Plantes, Long> {
    Page<Plantes> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Plantes> findAllByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);


}
