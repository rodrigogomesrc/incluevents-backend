package br.ufrn.imd.incluevents.dto;

import java.util.List;

import br.ufrn.imd.incluevents.model.Evento;

public class EventoGrupoVotacaoSeloDto {
  private Evento evento;
	private List<GrupoVotacaoSeloDto> gruposVotacaoSelo;

	public EventoGrupoVotacaoSeloDto(Evento evento, List<GrupoVotacaoSeloDto> gruposVotacaoSelo) {
		this.evento = evento;
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}

	public Evento getEvento() {
		return this.evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public List<GrupoVotacaoSeloDto> getGruposVotacaoSelo() {
		return gruposVotacaoSelo;
	}

	public void setGruposVotacaoSelo(List<GrupoVotacaoSeloDto> gruposVotacaoSelo) {
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}
}
