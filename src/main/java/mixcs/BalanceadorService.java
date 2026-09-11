package mixcs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalanceadorService {

    public List<Time> balancearTimes(List<Jogador> jogadores) {
        if (jogadores.size() != 10) {
            throw new IllegalArgumentException("É necessário ter exatamente 10 jogadores.");
        }

        // Ordena os jogadores pela média de votos
        jogadores.sort(Comparator.comparingDouble(Jogador::getHabilidadeMedia));

        List<Jogador> time1 = new ArrayList<>();
        List<Jogador> time2 = new ArrayList<>();

        double soma1 = 0;
        double soma2 = 0;

        // Distribuição Snake Draft
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
}