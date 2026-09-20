package mixcs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MixController {

    private final List<Jogador> lobby = new ArrayList<>();
    private final BalanceadorService balanceadorService = new BalanceadorService();
    // Estrutura: Votante -> (Alvo -> Tier)
    private final Map<String, Map<String, Double>> matrizVotos = new HashMap<>();

    @GetMapping("/jogadores")
    public List<Jogador> getJogadores() {
        return lobby;
    }

    @PostMapping("/entrar")
    public ResponseEntity<String> entrarLobby(@RequestParam String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome inválido!");
        }

        boolean existe = lobby.stream().anyMatch(j -> j.getNome().equalsIgnoreCase(nome));
        if (existe) {
            return ResponseEntity.badRequest().body("Jogador já cadastrado no lobby!");
        }

        if (lobby.size() >= 10) {
            return ResponseEntity.badRequest().body("Lobby cheio (máximo 10 jogadores)!");
        }

        lobby.add(new Jogador(nome));
        return ResponseEntity.ok("Jogador " + nome + " entrou no lobby!");
    }

    @PostMapping("/votar")
    public ResponseEntity<String> votarTier(@RequestParam String autor, @RequestParam String alvo, @RequestParam double tier) {
        if (lobby.size() < 10) {
            return ResponseEntity.badRequest().body("A votação só é liberada com 10 jogadores!");
        }

        Jogador jogadorAlvo = lobby.stream()
                .filter(j -> j.getNome().equalsIgnoreCase(alvo))
                .findFirst()
                .orElse(null);

        if (jogadorAlvo == null) {
            return ResponseEntity.badRequest().body("Jogador alvo não encontrado!");
        }

        jogadorAlvo.adicionarVoto(tier);

        // Salva na matriz: autor -> (alvo -> tier)
        matrizVotos.computeIfAbsent(autor, k -> new HashMap<>()).put(alvo, tier);

        return ResponseEntity.ok("Voto registrado!");
    }

    @GetMapping("/votos-concluidos-count")
    public ResponseEntity<Long> getVotosConcluidosCount() {
        long count = lobby.stream()
                .filter(j -> j.getHabilidadeMedia() != 3.0)
                .count();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/votos-detalhados")
    public ResponseEntity<Map<String, Map<String, Double>>> getVotosDetalhados() {
        return ResponseEntity.ok(matrizVotos);
    }

    @GetMapping("/balancear")
    public ResponseEntity<?> balancearPartida() {
        if (lobby.size() < 10) {
            return ResponseEntity.badRequest().body("É necessário ter 10 jogadores no lobby!");
        }

        try {
            List<Time> times = balanceadorService.balancearTimes(lobby);
            return ResponseEntity.ok(times);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetar")
    public ResponseEntity<String> resetarLobby() {
        lobby.clear();
        matrizVotos.clear();
        return ResponseEntity.ok("Lobby limpo com sucesso!");
    }
}