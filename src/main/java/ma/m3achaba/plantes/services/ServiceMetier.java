package ma.m3achaba.plantes.services;

import ma.m3achaba.plantes.common.PageResponse;

import java.util.List;
import java.util.Optional;

public interface ServiceMetier<R,Q> {
    Optional<R> findById(Long id);
    PageResponse<R> findAll(int page, int size);
    List<R> findAll();
    Optional<R> save(Q t);
    Optional<R> update(Q t,Long id);
    Optional<R> delete(Long id);
}
