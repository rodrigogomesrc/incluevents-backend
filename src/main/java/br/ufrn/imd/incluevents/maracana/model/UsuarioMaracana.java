package br.ufrn.imd.incluevents.maracana.model;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.maracana.model.enums.TipoUsuarioEnumMaracana;
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

    private String nomeDocumentacao;
    private String urlDocumentacao;
    private Boolean documentacaoValida;

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

    public String getNomeDocumentacao() {
		return this.nomeDocumentacao;
	}

	public void setNomeDocumentacao(String nomeDocumentacao) {
		this.nomeDocumentacao = nomeDocumentacao;
	}

	public String getUrlDocumentacao() {
		return this.urlDocumentacao;
	}

	public void setUrlDocumentacao(String urlDocumentacao) {
		this.urlDocumentacao = urlDocumentacao;
	}

    public Boolean getDocumentacaoValida() {
      return documentacaoValida;
    }

    public void setDocumentacaoValida(Boolean documentacaoValida) {
      this.documentacaoValida = documentacaoValida;
    }
}
