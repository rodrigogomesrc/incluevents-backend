package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.Selo;

public class GrupoVotacaoSeloDto {
  private Selo selo;
  private Long totalScore;
  private Long totalEnvios;
  private Long scorePositivo;
  private Long scoreNegativo;
  private Long enviosPositivos;
  private Long enviosNegativos;

  public GrupoVotacaoSeloDto(Selo selo, Long totalScore, Long totalEnvios, Long scorePositivo, Long scoreNegativo, Long enviosPositivos, Long enviosNegativos) {
    this.selo = selo;
    this.totalScore = totalScore;
    this.totalEnvios = totalEnvios;
    this.scorePositivo = scorePositivo;
    this.scoreNegativo = scoreNegativo;
    this.enviosPositivos = enviosPositivos;
    this.enviosNegativos = enviosNegativos;
  }

	public Selo getSelo() {
		return this.selo;
	}

	public void setSelo(Selo selo) {
		this.selo = selo;
	}

	public Long getTotalScore() {
		return this.totalScore;
	}

	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}

	public Long getTotalEnvios() {
		return this.totalEnvios;
	}

	public void setTotalEnvios(Long totalEnvios) {
		this.totalEnvios = totalEnvios;
	}

	public Long getScorePositivo() {
		return this.scorePositivo;
	}

	public void setScorePositivo(Long scorePositivo) {
		this.scorePositivo = scorePositivo;
	}

	public Long getScoreNegativo() {
		return this.scoreNegativo;
	}

	public void setScoreNegativo(Long scoreNegativo) {
		this.scoreNegativo = scoreNegativo;
	}

	public Long getEnviosPositivos() {
		return this.enviosPositivos;
	}

	public void setEnviosPositivos(Long enviosPositivos) {
		this.enviosPositivos = enviosPositivos;
	}

	public Long getEnviosNegativos() {
		return this.enviosNegativos;
	}

	public void setEnviosNegativos(Long enviosNegativos) {
		this.enviosNegativos = enviosNegativos;
	}
}
