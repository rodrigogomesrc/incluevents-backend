package br.ufrn.imd.incluevents.model;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "selo")
public class Selo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TipoSeloEnum tipoSelo;

    private Boolean validado;

    @OneToMany(mappedBy = "selo")
    private Set<Validacao> validacoes;

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

    public Set<Validacao> getValidacoes() {
        return validacoes;
    }

    public void setValidacoes(Set<Validacao> validacoes) {
        this.validacoes = validacoes;
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
