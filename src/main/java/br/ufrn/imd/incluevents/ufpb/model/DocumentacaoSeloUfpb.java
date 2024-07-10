package br.ufrn.imd.incluevents.ufpb.model;

import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "documentacao_selo_ufpb")
public class DocumentacaoSeloUfpb extends DocumentacaoSelo {
    private Boolean preValidada;

	public Boolean getPreValidada() {
		return this.preValidada;
	}

	public void setPreValidada(Boolean preValidada) {
		this.preValidada = preValidada;
	}
}
