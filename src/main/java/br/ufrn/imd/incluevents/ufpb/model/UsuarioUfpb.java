package br.ufrn.imd.incluevents.ufpb.model;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario_ufpb")
public class UsuarioUfpb extends Usuario {
    private String cargo;
    private int tempoServico;

    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnumUfpb tipo;

    public TipoUsuarioEnumUfpb getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoUsuarioEnumUfpb tipo) {
        this.tipo = tipo;
    }

    public String getCargo() {
        return this.cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getTempoServico() {
        return this.tempoServico;
    }

    public void setTempoServico(int tempoServico) {
        this.tempoServico = tempoServico;
    }
}
