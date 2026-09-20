package mixcs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findByAutorAndAlvo(Jogador autor, Jogador alvo);

    List<Voto> findByAlvo(Jogador alvo);

    // Conta quantos jogadores distintos já finalizaram seus votos (votaram em 9 pessoas)
    @Query("SELECT COUNT(DISTINCT v.autor) FROM Voto v")
    long countAutoresQueVotaram();
}