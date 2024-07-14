package br.ufrn.imd.incluevents.framework.dto;

import java.util.List;

import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Evento;

public record EventoDocumentacoesSeloDto(Evento evento, List<DocumentacaoSelo> documentacoesSelo) {
}
