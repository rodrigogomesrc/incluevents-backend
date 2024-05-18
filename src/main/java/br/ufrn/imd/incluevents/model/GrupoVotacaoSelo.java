package br.ufrn.imd.incluevents.model;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public interface GrupoVotacaoSelo {
  public int getIdselo();
  public TipoSeloEnum getTipoSelo();
  public int getTotalEnvios();
  public int getScorePositivo();
  public int getScoreNegativo();
}
