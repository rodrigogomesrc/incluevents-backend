package br.ufrn.imd.incluevents.natal.model;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.natal.model.enums.TipoUsuarioEnumNatal;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario_natal")
public class UsuarioNatal extends Usuario {
    private int reputacao;

    @Enumerated(EnumType.STRING)
    private TipoUsuarioEnumNatal tipo;

	public TipoUsuarioEnumNatal getTipo() {
		return this.tipo;
	}

	public void setTipo(TipoUsuarioEnumNatal tipo) {
		this.tipo = tipo;
	}

	public int getReputacao() {
		return this.reputacao;
	}

	public void setReputacao(int reputacao) {
		this.reputacao = reputacao;
	}
}
