package br.ufrn.imd.incluevents.framework.dto;

import java.util.List;

import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Estabelecimento;

public record EstabelecimentoDocumentacaoSeloDto(Estabelecimento estabelecimento, List<DocumentacaoSelo> documentacoesSelo) {

}
