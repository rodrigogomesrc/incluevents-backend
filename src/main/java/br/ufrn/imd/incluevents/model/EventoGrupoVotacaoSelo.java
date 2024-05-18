package br.ufrn.imd.incluevents.model;

import java.util.List;

public class EventoGrupoVotacaoSelo {
  private Evento evento;
	private List<GrupoVotacaoSelo> gruposVotacaoSelo;

	public EventoGrupoVotacaoSelo(Evento evento, List<GrupoVotacaoSelo> gruposVotacaoSelo) {
		this.evento = evento;
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}

	public Evento getEvento() {
		return this.evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public List<GrupoVotacaoSelo> getGruposVotacaoSelo() {
		return gruposVotacaoSelo;
	}

	public void setGruposVotacaoSelo(List<GrupoVotacaoSelo> gruposVotacaoSelo) {
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}
}
