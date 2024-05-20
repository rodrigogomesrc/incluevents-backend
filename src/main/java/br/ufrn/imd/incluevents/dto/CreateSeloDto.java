package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public record CreateSeloDto(TipoSeloEnum tipoSelo, Integer idEvento, Integer idEstabelecimento) {
}
