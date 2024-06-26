package br.ufrn.imd.incluevents.framework.dto;

import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;

public record CreateSeloDto(TipoSeloEnum tipoSelo, Integer idEvento, Integer idEstabelecimento) {
}
