package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public record CreateVotacaoSeloDto(Integer idEvento, Integer idEstabelecimento, String descricao, TipoSeloEnum tipoSelo, Boolean possuiSelo) {
}
