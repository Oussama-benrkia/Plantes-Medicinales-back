package ma.m3achaba.plantes.repo;

import ma.m3achaba.plantes.model.Token;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository {
    Optional<Token> findByToken(String token);
}
