package br.ufrn.imd.incluevents.model;

import java.util.List;

public class EstabelecimentoGrupoVotacaoSelo {
  private Estabelecimento estabelecimento;
	private List<GrupoVotacaoSelo> gruposVotacaoSelo;

	public EstabelecimentoGrupoVotacaoSelo(Estabelecimento estabelecimento, List<GrupoVotacaoSelo> gruposVotacaoSelo) {
		this.estabelecimento = estabelecimento;
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}

	public Estabelecimento getEstabelecimento() {
    return estabelecimento;
  }

  public void setEstabelecimento(Estabelecimento estabelecimento) {
    this.estabelecimento = estabelecimento;
  }

	public List<GrupoVotacaoSelo> getGruposVotacaoSelo() {
		return gruposVotacaoSelo;
	}

	public void setGruposVotacaoSelo(List<GrupoVotacaoSelo> gruposVotacaoSelo) {
		this.gruposVotacaoSelo = gruposVotacaoSelo;
	}
}
