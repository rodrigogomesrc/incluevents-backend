package br.ufrn.imd.incluevents.model;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "selo")
public class Selo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TipoSeloEnum tipoSelo;

    private Boolean validado;

    @JsonIgnore
    @OneToMany(mappedBy = "selo")
    private Set<VotacaoSelo> votacoesSelo;

    @ManyToOne
    @JoinColumn(name = "id_estabelecimento")
    @Nullable
    private Estabelecimento estabelecimento;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    @Nullable
    private Evento evento;

    public Selo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TipoSeloEnum getTipoSelo() {
        return tipoSelo;
    }

    public void setTipoSelo(TipoSeloEnum tipoSelo) {
        this.tipoSelo = tipoSelo;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Set<VotacaoSelo> getVotacoesSelo() {
      return votacoesSelo;
    }

    public void setVotacoesSelo(Set<VotacaoSelo> votacoesSelo) {
      this.votacoesSelo = votacoesSelo;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    @Override
    public String toString() {
        return "Selo{" +
                "id=" + id +
                ", tipoSelo=" + tipoSelo +
                ", validado=" + validado +
                '}';
    }
}
