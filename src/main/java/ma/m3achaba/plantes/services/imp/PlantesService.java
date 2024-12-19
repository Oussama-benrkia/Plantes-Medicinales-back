package ma.m3achaba.plantes.services.imp;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.dto.PlantesRequest;
import ma.m3achaba.plantes.dto.PlantesResponse;
import ma.m3achaba.plantes.mapper.MaladiesMapper;
import ma.m3achaba.plantes.mapper.PlantesMapper;
import ma.m3achaba.plantes.model.Maladies;
import ma.m3achaba.plantes.model.Plantes;
import ma.m3achaba.plantes.repo.MaladiesRepository;
import ma.m3achaba.plantes.repo.PlantesRepository;
import ma.m3achaba.plantes.services.ServiceMetier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlantesService implements ServiceMetier<PlantesResponse, PlantesRequest> {
    private final PlantesRepository plantesRepository;
    private final PlantesMapper plantesMapper;
    private final MaladiesRepository maladiesRepository;

    private Plantes findPlanteById(Long id) {
        return plantesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plante " + id + " not found"));
    }

    private PageResponse<PlantesResponse> createPageResponse(Page<Plantes> page) {
        List<PlantesResponse> list = page.getContent().stream()
                .map(plantesMapper::toResponse)
                .toList();

        return PageResponse.<PlantesResponse>builder()
                .totalElements(page.getTotalElements())
                .number(page.getNumber())
                .last(page.isLast())
                .first(page.isFirst())
                .content(list)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public Optional<PlantesResponse> findById(Long id) {
        return Optional.ofNullable(plantesMapper.toResponse(findPlanteById(id)));
    }

    @Override
    public PageResponse<PlantesResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Plantes> res = plantesRepository.findAll(pageable);
        return createPageResponse(res);
    }

    @Override
    public List<PlantesResponse> findAll() {
        return plantesRepository.findAll().stream()
                .map(plantesMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<PlantesResponse> save(PlantesRequest t) {
        if (plantesRepository.existsByName(t.name())) {
            throw new EntityNotFoundException("Plante " + t.name() + " already exists");
        }
        List<Long> maladieIds = t.maladie().stream()
                .map(Long::valueOf)
                .toList();
        List<Maladies> maladies = maladiesRepository.findAllById(maladieIds);
        if (maladies.size() != maladieIds.size()) {
            throw new EntityNotFoundException("Some Maladie IDs are not valid");
        }
        Plantes plante = plantesMapper.toEntity(t);
        plante.setMaladies(maladies);
        Plantes res = plantesRepository.save(plante);
        return Optional.ofNullable(plantesMapper.toResponse(res));
    }


    @Override
    public Optional<PlantesResponse> update(PlantesRequest t, Long id) {
        Plantes plantes = findPlanteById(id);
        boolean change = false;
        List<Long> maladieIds = t.maladie().stream()
                .map(Long::valueOf)
                .toList();
        List<Maladies> maladies = maladiesRepository.findAllById(maladieIds);
        if (maladies.size() != maladieIds.size()) {
            throw new EntityNotFoundException("Some Maladie IDs are not valid");
        }
        plantes.setMaladies(maladies);

        if (t.name() != null && !t.name().isEmpty() && !t.name().equals(plantes.getName())) {
            plantes.setName(t.name());
            change = true;
        }

        if (t.description() != null && !t.description().isEmpty() && !t.description().equals(plantes.getDescription())) {
            plantes.setDescription(t.description());
            change = true;
        }

        if (t.utilisation() != null && !t.utilisation().isEmpty() && !t.utilisation().equals(plantes.getUtilisation())) {
            plantes.setUtilisation(t.utilisation());
            change = true;
        }

        if (t.precautions() != null && !t.precautions().isEmpty() && !t.precautions().equals(plantes.getPrecautions())) {
            plantes.setPrecautions(t.precautions());
            change = true;
        }

        if (change) {
            plantes = plantesRepository.save(plantes);
        }
        return Optional.ofNullable(plantesMapper.toResponse(plantes));

    }

    @Override
    public Optional<PlantesResponse> delete(Long id) {
        Plantes plantes = findPlanteById(id);
        plantes.getMaladies().clear();
        plantesRepository.delete(plantes);
        return Optional.ofNullable(plantesMapper.toResponse(plantes));
    }

public PageResponse<PlantesResponse> findAllWithSearch(int page, int size, String search) {
            Pageable pageable = PageRequest.of(page, size);
        Page<Plantes> res = plantesRepository.findAllByNameContainingIgnoreCase(search, pageable);
        return createPageResponse(res);
    }
    public PageResponse<PlantesResponse> findAllWithsearch(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Plantes> res = plantesRepository.findAllByNameContainingIgnoreCase(search, pageable);
        return createPageResponse(res);
    }

    public List<PlantesResponse> findAllWithSearch(String search) {
        return plantesRepository.findAllByNameContainingIgnoreCase(search).stream()
                .map(plantesMapper::toResponse)
                .toList();
    }
}
