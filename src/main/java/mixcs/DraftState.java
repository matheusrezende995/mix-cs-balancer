package mixcs;

import java.util.List;

public class DraftState {
    private Jogador capitao1;
    private Jogador capitao2;
    private List<Jogador> disponiveis;
    private Time time1;
    private Time time2;
    private String turnoAtual; // Nome do capitão que escolhe no momento
    private boolean finalizado;

    public DraftState(Jogador capitao1, Jogador capitao2, List<Jogador> disponiveis, Time time1, Time time2) {
        this.capitao1 = capitao1;
        this.capitao2 = capitao2;
        this.disponiveis = disponiveis;
        this.time1 = time1;
        this.time2 = time2;
        this.turnoAtual = capitao1.getNome();
        this.finalizado = false;
    }

    // Getters e Setters
    public Jogador getCapitao1() { return capitao1; }
    public void setCapitao1(Jogador capitao1) { this.capitao1 = capitao1; }

    public Jogador getCapitao2() { return capitao2; }
    public void setCapitao2(Jogador capitao2) { this.capitao2 = capitao2; }

    public List<Jogador> getDisponiveis() { return disponiveis; }
    public void setDisponiveis(List<Jogador> disponiveis) { this.disponiveis = disponiveis; }

    public Time getTime1() { return time1; }
    public void setTime1(Time time1) { this.time1 = time1; }

    public Time getTime2() { return time2; }
    public void setTime2(Time time2) { this.time2 = time2; }

    public String getTurnoAtual() { return turnoAtual; }
    public void setTurnoAtual(String turnoAtual) { this.turnoAtual = turnoAtual; }

    public boolean isFinalizado() { return finalizado; }
    public void setFinalizado(boolean finalizado) { this.finalizado = finalizado; }
}