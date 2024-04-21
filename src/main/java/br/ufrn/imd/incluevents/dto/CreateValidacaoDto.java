package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public record CreateValidacaoDto(Integer idEvento, Integer idEstabelecimento, String descricao, Integer idUsuario, TipoSeloEnum tipoSelo) {
}
