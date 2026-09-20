package mixcs;

import jakarta.persistence.*;

@Entity
@Table(name = "votos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"autor_id", "alvo_id"})
})
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Jogador autor;

    @ManyToOne
    @JoinColumn(name = "alvo_id", nullable = false)
    private Jogador alvo;

    @Column(nullable = false)
    private Double tier;

    public Voto() {}

    public Voto(Jogador autor, Jogador alvo, Double tier) {
        this.autor = autor;
        this.alvo = alvo;
        this.tier = tier;
    }

    public Long getId() {
        return id;
    }

    public Jogador getAutor() {
        return autor;
    }

    public Jogador getAlvo() {
        return alvo;
    }

    public Double getTier() {
        return tier;
    }

    public void setTier(Double tier) {
        this.tier = tier;
    }
}