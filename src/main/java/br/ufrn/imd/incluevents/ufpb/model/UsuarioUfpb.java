package br.ufrn.imd.incluevents.ufpb.model;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.ufpb.model.enums.CargoEnumUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario_ufpb")
public class UsuarioUfpb extends Usuario {
    @Enumerated(EnumType.STRING)
    private CargoEnumUfpb cargo;

    private Double tempoServico;

    private Double imc;

    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnumUfpb tipo;

    public TipoUsuarioEnumUfpb getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoUsuarioEnumUfpb tipo) {
        this.tipo = tipo;
    }

    public CargoEnumUfpb getCargo() {
        return this.cargo;
    }

    public void setCargo(CargoEnumUfpb cargo) {
        this.cargo = cargo;
    }

    public Double getTempoServico() {
        return this.tempoServico;
    }

    public void setTempoServico(Double tempoServico) {
        this.tempoServico = tempoServico;
    }
    public Double getImc() {
        return this.imc;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }
}
