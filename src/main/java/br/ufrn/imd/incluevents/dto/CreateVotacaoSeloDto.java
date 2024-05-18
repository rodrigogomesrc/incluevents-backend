package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public record CreateVotacaoSeloDto(Integer idEvento, Integer idEstabelecimento, String descricao, Integer idUsuario, TipoSeloEnum tipoSelo, Boolean possuiSelo) {
}
