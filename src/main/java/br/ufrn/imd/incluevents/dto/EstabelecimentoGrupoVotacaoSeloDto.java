package br.ufrn.imd.incluevents.dto;

import java.util.List;

import br.ufrn.imd.incluevents.model.Estabelecimento;

public class EstabelecimentoGrupoVotacaoSeloDto {
  private Estabelecimento estabelecimento;
	private List<GrupoVotacaoSeloDto> gruposVotacaoSelo;

	public EstabelecimentoGrupoVotacaoSeloDto(Estabelecimento estabelecimento, List<GrupoVotacaoSeloDto> gruposVotacaoSelo) {
		this.estabelecimento = estabelecimento;
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}

	public Estabelecimento getEstabelecimento() {
    return estabelecimento;
  }

  public void setEstabelecimento(Estabelecimento estabelecimento) {
    this.estabelecimento = estabelecimento;
  }

	public List<GrupoVotacaoSeloDto> getGruposVotacaoSelo() {
		return gruposVotacaoSelo;
	}

	public void setGruposVotacaoSelo(List<GrupoVotacaoSeloDto> gruposVotacaoSelo) {
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}
}
