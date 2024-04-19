package br.ufrn.imd.incluevents.model;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import jakarta.persistence.*;

@Entity
@Table(name = "selo")
public class Selo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TipoSeloEnum tipoSelo;

    private Integer idEvento;

    private Integer idEstab;

    private Boolean validado;

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

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public Integer getIdEstab() {
        return idEstab;
    }

    public void setIdEstab(Integer idEstab) {
        this.idEstab = idEstab;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    @Override
    public String toString() {
        return "Selo{" +
                "id=" + id +
                ", tipoSelo=" + tipoSelo +
                ", idEvento=" + idEvento +
                ", idEstab=" + idEstab +
                ", validado=" + validado +
                '}';
    }
}
