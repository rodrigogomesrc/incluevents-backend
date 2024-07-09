package br.ufrn.imd.incluevents.macarana.model;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.macarana.model.enums.TipoUsuarioEnumMaracana;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "usuario_maracana")
public class UsuarioMaracana extends Usuario {
    private Date criadoEm;

    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnumMaracana tipo;

    public TipoUsuarioEnumMaracana getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoUsuarioEnumMaracana tipo) {
        this.tipo = tipo;
    }

    public Date getCriadoEm() {
        return this.criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }
}
