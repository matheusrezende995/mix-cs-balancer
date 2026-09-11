package mixcs;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MixController {

    private final List<Jogador> lobby = new ArrayList<>();
    private final BalanceadorService balanceadorService = new BalanceadorService();

    @GetMapping("/jogadores")
    public List<Jogador> listarJogadores() {
        return lobby;
    }

    @PostMapping("/entrar")
    public String entrarNoLobby(@RequestParam String nome) {
        if (lobby.size() >= 10) {
            return "Lobby cheio!";
        }

        // Verifica se o nick já existe no lobby
        for (Jogador j : lobby) {
            if (j.getNome().equalsIgnoreCase(nome)) {
                return "Nick já está no lobby!";
            }
        }

        lobby.add(new Jogador(nome));
        return nome + " entrou no lobby!";
    }

    @PostMapping("/votar")
    public String votar(@RequestParam String alvo, @RequestParam double tier) {
        for (Jogador j : lobby) {
            if (j.getNome().equalsIgnoreCase(alvo)) {
                j.adicionarVoto(tier);
                return "Voto registrado para " + alvo;
            }
        }
        return "Jogador não encontrado!";
    }

    @GetMapping("/balancear")
    public List<Time> balancearPartida() {
        return balanceadorService.balancearTimes(lobby);
    }
}