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

    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @Column(name = "votou", nullable = false)
    private boolean votou = false;

    private double habilidadeMedia = 3.0;

    @Transient
    private List<Voto> votosRecebidos = new ArrayList<>();

    public Jogador() {}

    public Jogador(String nome) {
        this.nome = nome;
        this.votou = false;
        this.habilidadeMedia = 3.0;
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

    public String getNickname() {
        return nome;
    }

    public void setNickname(String nickname) {
        this.nome = nickname;
    }

    public boolean isVotou() {
        return votou;
    }

    public void setVotou(boolean votou) {
        this.votou = votou;
    }

    public double getHabilidadeMedia() {
        return habilidadeMedia;
    }

    public void setHabilidadeMedia(double habilidadeMedia) {
        this.habilidadeMedia = habilidadeMedia;
    }

    public List<Voto> getVotosRecebidos() {
        return votosRecebidos;
    }

    public void setVotosRecebidos(List<Voto> votosRecebidos) {
        this.votosRecebidos = votosRecebidos;
    }
}