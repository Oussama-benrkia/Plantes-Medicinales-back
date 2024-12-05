package ma.m3achaba.plantes.services.imp;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.mapper.MaladiesMapper;
import ma.m3achaba.plantes.model.Maladies;
import ma.m3achaba.plantes.repo.MaladiesRepository;
import ma.m3achaba.plantes.services.ServiceMetier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Transactional
@RequiredArgsConstructor
public class MaladiesService implements ServiceMetier<MaladiesResponse, MaladiesRequest> {
    private final MaladiesRepository maladiesRepository;
    private final MaladiesMapper maladiesMapper;

    private Maladies findMaladieById(Long id) {
        return maladiesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("maladie " + id + " not found"));
    }

    private PageResponse<MaladiesResponse> createPageResponse(Page<Maladies> page) {
        List<MaladiesResponse> list = page.getContent().stream()
                .map(maladiesMapper::toResponse)
                .toList();

        return PageResponse.<MaladiesResponse>builder()
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
    public Optional<MaladiesResponse> findById(Long id) {
        return Optional.ofNullable(maladiesMapper.toResponse(findMaladieById(id)));
    }

    @Override
    public PageResponse<MaladiesResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Maladies> res = maladiesRepository.findAll(pageable);
        return createPageResponse(res);
    }

    @Override
    public List<MaladiesResponse> findAll() {
        return maladiesRepository.findAll().stream()
                .map(maladiesMapper::toResponse)
                .toList();
    }

    @Override
    public Optional<MaladiesResponse> save(MaladiesRequest t) {
        if (maladiesRepository.existsByName(t.nom())) {
            throw new EntityNotFoundException("maladie " + t.nom() + " already exists");
        }
        Maladies res = maladiesRepository.save(maladiesMapper.toEntity(t));
        return Optional.ofNullable(maladiesMapper.toResponse(res));
    }

    @Override
    public Optional<MaladiesResponse> update(MaladiesRequest t, Long id) {
        Maladies maladies = findMaladieById(id);
        boolean change = false;

        if (t.nom() != null && !t.nom().isEmpty() && !t.nom().equals(maladies.getName())) {
            maladies.setName(t.nom());
            change = true;
        }

        if (change) {
            maladies = maladiesRepository.save(maladies);
        }
        return Optional.ofNullable(maladiesMapper.toResponse(maladies));
    }

    @Override
    public Optional<MaladiesResponse> delete(Long id) {
        Maladies maladies = findMaladieById(id);
        maladiesRepository.delete(maladies);
        return Optional.ofNullable(maladiesMapper.toResponse(maladies));
    }

    public PageResponse<MaladiesResponse> findAllWithsearch(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Maladies> res = maladiesRepository.findAllByNameContainingIgnoreCase(search, pageable);
        return createPageResponse(res);
    }

    public List<MaladiesResponse> findAllWithsearch(String search) {
        return maladiesRepository.findAllByNameContainingIgnoreCase(search).stream()
                .map(maladiesMapper::toResponse)
                .toList();
    }
}