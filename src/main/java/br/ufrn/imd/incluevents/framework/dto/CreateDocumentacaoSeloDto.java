package br.ufrn.imd.incluevents.framework.dto;

import org.springframework.web.multipart.MultipartFile;

import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;

public record CreateDocumentacaoSeloDto(Integer idEvento, Integer idEstabelecimento, TipoSeloEnum tipoSelo, MultipartFile arquivo) {
}
