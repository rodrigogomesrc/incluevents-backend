package br.ufrn.imd.incluevents.framework.dto;

import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;

public record CreateVotacaoSeloDto(Integer idEvento, Integer idEstabelecimento, String descricao, TipoSeloEnum tipoSelo, Boolean possuiSelo) {
}
