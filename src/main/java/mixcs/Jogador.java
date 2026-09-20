package mixcs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "jogadores")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @JsonIgnore
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private List<Voto> votosDados;

    @JsonIgnore
    @OneToMany(mappedBy = "alvo", cascade = CascadeType.ALL)
    private List<Voto> votosRecebidos;

    public Jogador() {
    }

    public Jogador(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Voto> getVotosDados() {
        return votosDados;
    }

    public void setVotosDados(List<Voto> votosDados) {
        this.votosDados = votosDados;
    }

    public List<Voto> getVotosRecebidos() {
        return votosRecebidos;
    }

    public void setVotosRecebidos(List<Voto> votosRecebidos) {
        this.votosRecebidos = votosRecebidos;
    }

    public double getHabilidadeMedia() {
        if (votosRecebidos == null || votosRecebidos.isEmpty()) {
            return 0.0;
        }
        double soma = 0;
        for (Voto v : votosRecebidos) {
            soma += v.getTier();
        }
        return soma / votosRecebidos.size();
    }
}