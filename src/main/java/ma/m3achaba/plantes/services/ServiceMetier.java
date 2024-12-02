package ma.m3achaba.plantes.services;

import java.util.List;
import java.util.Optional;

public interface ServiceMetier<R,Q> {
    Optional<R> findById(Long id);
    List<R> findAll();
    Optional<R> save(Q t);
    Optional<R> update(Q t,Long id);
    Optional<R> delete(Long id);
}
