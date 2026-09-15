package mixcs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MixController {

    private final List<Jogador> lobby = new ArrayList<>();
    private final BalanceadorService balanceadorService = new BalanceadorService();

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
    public ResponseEntity<String> votarTier(@RequestParam String alvo, @RequestParam double tier) {
        // REGRA: Só permite votar se o lobby tiver exatamente 10 jogadores
        if (lobby.size() < 10) {
            return ResponseEntity.badRequest().body("A votação só é liberada quando o lobby tiver 10 jogadores!");
        }

        Jogador jogadorAlvo = lobby.stream()
                .filter(j -> j.getNome().equalsIgnoreCase(alvo))
                .findFirst()
                .orElse(null);

        if (jogadorAlvo == null) {
            return ResponseEntity.badRequest().body("Jogador alvo não encontrado!");
        }

        jogadorAlvo.adicionarVoto(tier);
        return ResponseEntity.ok("Voto registrado para " + alvo);
    }

    @GetMapping("/balancear")
    public ResponseEntity<?> balancearPartida() {
        if (lobby.size() < 10) {
            return ResponseEntity.badRequest().body("É necessário ter 10 jogadores no lobby para balancear!");
        }

        try {
            List<Time> times = balanceadorService.balancearTimes(lobby);
            return ResponseEntity.ok(times);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/draft/estado")
    public ResponseEntity<?> getEstadoDraft() {
        return ResponseEntity.ok(balanceadorService.getDraftAtual());
    }

    @PostMapping("/draft/iniciar")
    public ResponseEntity<?> iniciarDraft() {
        // REGRA: Só permite iniciar Draft com 10 jogadores
        if (lobby.size() < 10) {
            return ResponseEntity.badRequest().body("É necessário ter 10 jogadores no lobby para iniciar o Draft!");
        }

        try {
            DraftState state = balanceadorService.iniciarDraft(lobby);
            return ResponseEntity.ok(state);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/draft/pick")
    public ResponseEntity<?> pickJogador(@RequestParam String capitao, @RequestParam String escolhido) {
        try {
            DraftState state = balanceadorService.pickJogador(capitao, escolhido);
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetar")
    public ResponseEntity<String> resetarLobby() {
        lobby.clear();
        return ResponseEntity.ok("Lobby limpo com sucesso!");
    }
}