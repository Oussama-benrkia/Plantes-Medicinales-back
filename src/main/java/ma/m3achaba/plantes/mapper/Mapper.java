package ma.m3achaba.plantes.mapper;

public interface Mapper<E,R,Q> {
    E toEntity(Q query);
    R toResponse(E entity);
}
