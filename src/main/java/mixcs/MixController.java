package mixcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MixController {

    @Autowired
    private JogadorRepository jogadorRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final BalanceadorService balanceadorService = new BalanceadorService();

    @GetMapping("/jogadores")
    public List<Jogador> getJogadores() {
        return jogadorRepository.findAll();
    }

    @PostMapping("/entrar")
    public ResponseEntity<String> entrarLobby(@RequestParam String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido!");
        }

        if (jogadorRepository.existsByNomeIgnoreCase(nome)) {
            throw new IllegalArgumentException("Jogador já cadastrado no lobby!");
        }

        if (jogadorRepository.count() >= 10) {
            throw new IllegalArgumentException("Lobby cheio (máximo 10 jogadores)!");
        }

        jogadorRepository.save(new Jogador(nome.trim()));

        notificarAtualizacaoGeral();

        return ResponseEntity.ok("Jogador " + nome + " entrou no lobby!");
    }

    @PostMapping("/votar")
    public ResponseEntity<String> votarTier(@RequestParam String autor, @RequestParam String alvo, @RequestParam double tier) {
        if (jogadorRepository.count() < 10) {
            throw new IllegalStateException("A votação só é liberada com 10 jogadores!");
        }

        Optional<Jogador> autorOpt = jogadorRepository.findByNomeIgnoreCase(autor);
        Optional<Jogador> alvoOpt = jogadorRepository.findByNomeIgnoreCase(alvo);

        if (autorOpt.isEmpty() || alvoOpt.isEmpty()) {
            throw new IllegalArgumentException("Jogador autor ou alvo não foi encontrado!");
        }

        Jogador autorObj = autorOpt.get();
        Jogador alvoObj = alvoOpt.get();

        if (autorObj.getId().equals(alvoObj.getId())) {
            throw new IllegalArgumentException("Não é permitido votar em si mesmo!");
        }

        Optional<Voto> votoExistente = votoRepository.findByAutorAndAlvo(autorObj, alvoObj);
        if (votoExistente.isPresent()) {
            Voto v = votoExistente.get();
            v.setTier(tier);
            votoRepository.save(v);
        } else {
            votoRepository.save(new Voto(autorObj, alvoObj, tier));
        }

        notificarAtualizacaoGeral();

        return ResponseEntity.ok("Voto registrado com sucesso!");
    }

    @GetMapping("/votos-concluidos-count")
    public ResponseEntity<Long> getVotosConcluidosCount() {
        return ResponseEntity.ok(votoRepository.countAutoresQueVotaram());
    }

    @GetMapping("/votos-detalhados")
    public ResponseEntity<Map<String, Map<String, Double>>> getVotosDetalhados() {
        Map<String, Map<String, Double>> matriz = new HashMap<>();
        List<Voto> todosVotos = votoRepository.findAll();

        for (Voto v : todosVotos) {
            matriz.computeIfAbsent(v.getAutor().getNome(), k -> new HashMap<>())
                    .put(v.getAlvo().getNome(), v.getTier());
        }

        return ResponseEntity.ok(matriz);
    }

    @GetMapping("/balancear")
    public ResponseEntity<List<Time>> balancearPartida() {
        List<Jogador> lobby = jogadorRepository.findAll();
        if (lobby.size() < 10) {
            throw new IllegalStateException("É necessário ter 10 jogadores no lobby!");
        }

        for (Jogador j : lobby) {
            List<Voto> votosRecebidos = votoRepository.findByAlvo(j);
            double media = votosRecebidos.stream()
                    .mapToDouble(Voto::getTier)
                    .average()
                    .orElse(3.0);
            // Se precisar setar a média no objeto jogador dinamicamente para o balanceador usar:
            j.setHabilidadeMedia(media);
        }

        List<Time> times = balanceadorService.balancearTimes(lobby);
        notificarAtualizacaoGeral();
        return ResponseEntity.ok(times);
    }

    @PostMapping("/resetar")
    public ResponseEntity<String> resetarLobby() {
        votoRepository.deleteAll();
        jogadorRepository.deleteAll();

        notificarAtualizacaoGeral();

        return ResponseEntity.ok("Lobby e votos limpos no banco de dados!");
    }

    @GetMapping("/status-votacao")
    public ResponseEntity<Map<String, Object>> obterStatusVotacao() {
        List<Jogador> todos = jogadorRepository.findAll();

        List<Long> idsAutoresQueVotaram = votoRepository.findAll().stream()
                .map(v -> v.getAutor().getId())
                .distinct()
                .toList();

        List<String> votaram = new ArrayList<>();
        List<String> faltam = new ArrayList<>();

        for (Jogador j : todos) {
            if (idsAutoresQueVotaram.contains(j.getId())) {
                votaram.add(j.getNome());
            } else {
                faltam.add(j.getNome());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("votaram", votaram);
        response.put("faltam", faltam);
        response.put("totalVotaram", votaram.size());
        response.put("totalJogadores", todos.size());

        return ResponseEntity.ok(response);
    }

    private void notificarAtualizacaoGeral() {
        try {
            ResponseEntity<Map<String, Object>> responseEntity = obterStatusVotacao();
            messagingTemplate.convertAndSend("/topic/status", responseEntity.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}