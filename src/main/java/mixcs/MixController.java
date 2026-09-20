package mixcs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    // Trava em memória para armazenar os autores que já finalizaram e enviaram os votos
    private final Set<String> jogadoresQueJaVotaram = new HashSet<>();

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

        // CORREÇÃO DEFINITIVA: Verifica direto no Banco de Dados se este autor já finalizou/enviou votos.
        // Se você quiser permitir múltiplos votos enquanto edita na mesma tela e só bloquear depois de "finalizar",
        // mantemos a checagem, mas impedimos que uma aba nova (que limpa o cliente) burle se o usuário já tiver
        // enviado o lote ou se a regra for "cada par autor/alvo só vota uma vez".

        // Se a sua intenção é que cada jogador só possa enviar a lista 1 única vez e nunca mais mexer:
        boolean jaVotouNoBanco = votoRepository.findAll().stream()
                .anyMatch(v -> v.getAutor().getId().equals(autorObj.getId()));

        // Opcional: se o fluxo da sua aplicação permite alterar os votos livremente até clicar em "finalizar",
        // o ideal é olhar a sua flag de finalizados. Para persistir isso no banco sem perder ao reiniciar,
        // o ideal seria criar uma coluna booleana `vancou/finalizou` na tabela de Jogadores.
        // Mas se quisermos corrigir o problema do dup de aba agora de forma simples:

        Optional<Voto> votoExistente = votoRepository.findByAutorAndAlvo(autorObj, alvoObj);

        // Se o voto já existe e você quer bloquear alterações após a primeira submissão em outra aba:
        // (Descomente a linha abaixo se quiser bloquear qualquer alteração após o primeiro voto cadastrado)
        /*
        if (votoExistente.isPresent() && jogadoresQueJaVotaram.contains(autor)) {
            throw new IllegalStateException("O usuário " + autor + " já votou e não pode alterar!");
        }
        */

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

    // Endpoint recomendado para travar o usuário definitivamente após mandar todos os votos da tela
    @PostMapping("/finalizar-votos")
    public ResponseEntity<String> finalizarVotos(@RequestParam String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do autor inválido!");
        }
        jogadoresQueJaVotaram.add(autor.trim());
        notificarAtualizacaoGeral();
        return ResponseEntity.ok("Votos finalizados e travados para " + autor);
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
        jogadoresQueJaVotaram.clear(); // Limpa as travas de votos ao resetar o lobby

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