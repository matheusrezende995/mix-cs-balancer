package mixcs;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<Jogador> findByNomeIgnoreCase(String nome);
}