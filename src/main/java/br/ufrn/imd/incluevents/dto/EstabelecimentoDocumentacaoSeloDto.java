package br.ufrn.imd.incluevents.dto;

import java.util.List;

import br.ufrn.imd.incluevents.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.model.Estabelecimento;

public record EstabelecimentoDocumentacaoSeloDto(Estabelecimento estabelecimento, List<DocumentacaoSelo> documentacoesSelo) {

}
