package br.ufrn.imd.incluevents.dto;

import org.springframework.web.multipart.MultipartFile;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

public record CreateDocumentacaoSeloDto(Integer idEvento, Integer idEstabelecimento, TipoSeloEnum tipoSelo, MultipartFile arquivo) {
}
