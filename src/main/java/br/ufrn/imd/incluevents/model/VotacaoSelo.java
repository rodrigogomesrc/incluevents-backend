package br.ufrn.imd.incluevents.model;

import jakarta.persistence.*;

@Entity
@Table(name = "validacao_selo")
public class VotacaoSelo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String descricao;
    private boolean possuiSelo;
    private int score;
    private boolean verificado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_selo")
    private Selo selo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean getPossuiSelo() {
        return possuiSelo;
    }

    public void setPossuiSelo(boolean possuiSelo) {
        this.possuiSelo = possuiSelo;
    }

    public int getScore() {
      return score;
    }

    public void setScore(int score) {
      this.score = score;
    }

    public boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Selo getSelo() {
        return selo;
    }

    public void setSelo(Selo selo) {
        this.selo = selo;
    }
}
