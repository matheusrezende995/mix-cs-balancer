package mixcs;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jogadores")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "alvo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votosRecebidos = new ArrayList<>();

    public Jogador() {}

    public Jogador(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Voto> getVotosRecebidos() {
        return votosRecebidos;
    }

    // Método exigido pelo BalanceadorService para calcular a média de habilidade/tier
    public double getHabilidadeMedia() {
        if (votosRecebidos == null || votosRecebidos.isEmpty()) {
            return 0.0;
        }
        double soma = 0.0;
        for (Voto v : votosRecebidos) {
            soma += v.getTier();
        }
        return soma / votosRecebidos.size();
    }
}