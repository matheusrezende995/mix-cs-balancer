package mixcs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalanceadorService {

    private DraftState draftAtual;

    public List<Time> balancearTimes(List<Jogador> jogadores) {
        if (jogadores.size() != 10) {
            throw new IllegalArgumentException("É necessário ter exatamente 10 jogadores no lobby.");
        }

        // Valida se todos possuem habilidade média cadastrada (> 0)
        boolean todosVotados = jogadores.stream().allMatch(j -> j.getHabilidadeMedia() > 0);
        if (!todosVotados) {
            throw new IllegalStateException("Todos os 10 jogadores precisam ser votados antes de realizar o balanceamento!");
        }

        jogadores.sort(Comparator.comparingDouble(Jogador::getHabilidadeMedia));

        List<Jogador> time1 = new ArrayList<>();
        List<Jogador> time2 = new ArrayList<>();
        double soma1 = 0;
        double soma2 = 0;

        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            if (soma1 <= soma2) {
                if (time1.size() < 5) {
                    time1.add(j);
                    soma1 += j.getHabilidadeMedia();
                } else {
                    time2.add(j);
                    soma2 += j.getHabilidadeMedia();
                }
            } else {
                if (time2.size() < 5) {
                    time2.add(j);
                    soma2 += j.getHabilidadeMedia();
                } else {
                    time1.add(j);
                    soma1 += j.getHabilidadeMedia();
                }
            }
        }

        List<Time> partida = new ArrayList<>();
        partida.add(new Time("Counter-Terrorists", time1));
        partida.add(new Time("Terrorists", time2));

        return partida;
    }

    // --- MODO CAPITÃES (DRAFT) -----

    public DraftState iniciarDraft(List<Jogador> lobby) {
        if (lobby.size() != 10) {
            throw new IllegalStateException("É necessário ter exatamente 10 jogadores no lobby para iniciar o Draft!");
        }

        boolean todosVotados = lobby.stream().allMatch(j -> j.getHabilidadeMedia() > 0);
        if (!todosVotados) {
            throw new IllegalStateException("Todos os jogadores precisam receber pelo menos um voto de Tier antes de iniciar!");
        }

        List<Jogador> ordenados = new ArrayList<>(lobby);
        ordenados.sort(Comparator.comparingDouble(Jogador::getHabilidadeMedia));

        Jogador c1 = ordenados.remove(0);
        Jogador c2 = ordenados.remove(0);

        List<Jogador> p1 = new ArrayList<>();
        p1.add(c1);

        List<Jogador> p2 = new ArrayList<>();
        p2.add(c2);

        Time time1 = new Time("Time " + c1.getNome(), p1);
        Time time2 = new Time("Time " + c2.getNome(), p2);

        this.draftAtual = new DraftState(c1, c2, ordenados, time1, time2);
        return draftAtual;
    }

    public DraftState pickJogador(String nomeCapitao, String nomeEscolhido) {
        if (draftAtual == null || draftAtual.isFinalizado()) {
            throw new IllegalStateException("Nenhum draft em andamento.");
        }

        if (!draftAtual.getTurnoAtual().equalsIgnoreCase(nomeCapitao)) {
            throw new IllegalArgumentException("Não é o seu turno de escolher! Aguarde a vez de: " + draftAtual.getTurnoAtual());
        }

        Jogador escolhido = draftAtual.getDisponiveis().stream()
                .filter(j -> j.getNome().equalsIgnoreCase(nomeEscolhido))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jogador não encontrado ou já escolhido."));

        draftAtual.getDisponiveis().remove(escolhido);

        if (nomeCapitao.equalsIgnoreCase(draftAtual.getCapitao1().getNome())) {
            draftAtual.getTime1().getJogadores().add(escolhido);
            draftAtual.setTurnoAtual(draftAtual.getCapitao2().getNome());
        } else {
            draftAtual.getTime2().getJogadores().add(escolhido);
            draftAtual.setTurnoAtual(draftAtual.getCapitao1().getNome());
        }

        if (draftAtual.getDisponiveis().isEmpty()) {
            draftAtual.setFinalizado(true);
        }

        return draftAtual;
    }

    public DraftState getDraftAtual() {
        return draftAtual;
    }
}