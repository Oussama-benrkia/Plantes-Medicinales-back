package ma.m3achaba.plantes.controller;

import lombok.RequiredArgsConstructor;
import ma.m3achaba.plantes.common.PageResponse;
import ma.m3achaba.plantes.dto.MaladiesRequest;
import ma.m3achaba.plantes.dto.MaladiesResponse;
import ma.m3achaba.plantes.exception.ResourceNotFoundException;
import ma.m3achaba.plantes.services.imp.MaladiesService;
import ma.m3achaba.plantes.validation.OnCreate;
import ma.m3achaba.plantes.validation.OnUpdate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maladies")
@RequiredArgsConstructor
public class MaladiesController {
    private final MaladiesService  maladiesService;

    @GetMapping("/{id}")
    public ResponseEntity<MaladiesResponse> findById(@PathVariable Long id) {
        return maladiesService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Maladies not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<MaladiesResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        PageResponse<MaladiesResponse> response = (search != null && !search.isEmpty())
                ? maladiesService.findAllWithsearch(page, size, search)
                : maladiesService.findAll(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<MaladiesResponse>> findAllMaladies(
            @RequestParam(required = false) String search
    ) {
        List<MaladiesResponse> responses = (search != null && !search.isEmpty())
                ? maladiesService.findAllWithsearch(search)
                : maladiesService.findAll();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaladiesResponse saveMaladie(
            @Validated(OnCreate.class) @RequestBody MaladiesRequest request
    ) {
        return maladiesService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to save Maladies"));
    }

    @PutMapping("/{id}")
    public MaladiesResponse updateMaladie(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody MaladiesRequest request
    ) {
        return maladiesService.update(request, id)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to update Maladies with id: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaladie(@PathVariable Long id) {
        maladiesService.delete(id);
    }
}