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

        boolean todosVotados = jogadores.stream().allMatch(j -> j.getHabilidadeMedia() > 0);
        if (!todosVotados) {
            throw new IllegalStateException("Todos os 10 jogadores precisam ser votados antes de realizar o balanceamento!");
        }

        // Encontra a melhor combinação 5v5 entre as 252 possibilidades possíveis
        List<Jogador> melhorTime1 = new ArrayList<>();
        List<Jogador> melhorTime2 = new ArrayList<>();
        double menorDiferenca = Double.MAX_VALUE;

        // Total de 10 jogadores, escolhemos 5 para formar o Time 1 (C(10,5) = 252 combinações)
        List<List<Jogador>> combinacoesTime1 = gerarCombinacoes(jogadores, 5);

        for (List<Jogador> t1 : combinacoesTime1) {
            List<Jogador> t2 = new ArrayList<>(jogadores);
            t2.removeAll(t1); // O restante forma o Time 2

            double soma1 = t1.stream().mapToDouble(Jogador::getHabilidadeMedia).sum();
            double soma2 = t2.stream().mapToDouble(Jogador::getHabilidadeMedia).sum();
            double diferenca = Math.abs(soma1 - soma2);

            // Se encontrarmos uma diferença menor, esta passa a ser a melhor divisão
            if (diferenca < menorDiferenca) {
                menorDiferenca = diferenca;
                melhorTime1 = t1;
                melhorTime2 = t2;
            }
        }

        // Opcional: Ordenar os jogadores dentro de cada time por habilidade para manter a organização visual
        melhorTime1.sort(Comparator.comparingDouble(Jogador::getHabilidadeMedia).reversed());
        melhorTime2.sort(Comparator.comparingDouble(Jogador::getHabilidadeMedia).reversed());

        List<Time> partida = new ArrayList<>();
        partida.add(new Time("Counter-Terrorists", melhorTime1));
        partida.add(new Time("Terrorists", melhorTime2));

        return partida;
    }

    /**
     * Método auxiliar recursivo para gerar todas as combinações possíveis de tamanho K a partir de uma lista.
     */
    private List<List<Jogador>> gerarCombinacoes(List<Jogador> jogadores, int k) {
        List<List<Jogador>> resultado = new ArrayList<>();
        combinarRecursivo(jogadores, k, 0, new ArrayList<>(), resultado);
        return resultado;
    }

    private void combinarRecursivo(List<Jogador> jogadores, int k, int inicio, List<Jogador> atual, List<List<Jogador>> resultado) {
        if (atual.size() == k) {
            resultado.add(new ArrayList<>(atual));
            return;
        }
        for (int i = inicio; i < jogadores.size(); i++) {
            atual.add(jogadores.get(i));
            combinarRecursivo(jogadores, k, i + 1, atual, resultado);
            atual.remove(atual.size() - 1);
        }
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