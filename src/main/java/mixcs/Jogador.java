package mixcs;

import java.util.ArrayList;
import java.util.List;

public class Jogador {
    private String nome;
    private List<Double> votosHabilidade = new ArrayList<>();

    public Jogador(String nome) {
        this.nome = nome;
    }

    public void adicionarVoto(double tier) {
        this.votosHabilidade.add(tier);
    }

    public double getHabilidadeMedia() {
        if (votosHabilidade.isEmpty()) {
            return 3.0; // Valor padrão se ainda não recebeu votos
        }
        double soma = 0;
        for (double voto : votosHabilidade) {
            soma += voto;
        }
        return soma / votosHabilidade.size();
    }

    public String getNome() {
        return nome;
    }
}