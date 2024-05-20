package br.ufrn.imd.incluevents.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.ufrn.imd.incluevents.dto.CreateDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.dto.EventoDocumentacoesSeloDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.service.DocumentacaoSeloService;

@RestController
@RequestMapping("documentacoes-selo")
public class DocumentacaoSeloController {
    private final DocumentacaoSeloService documentacaoSeloService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentacaoSeloController.class);

    public DocumentacaoSeloController(DocumentacaoSeloService documentacaoSeloService) {
        this.documentacaoSeloService = documentacaoSeloService;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto) {
        try {
            Usuario usuario = GetUsuarioLogado.getUsuarioLogado();

            System.out.println(usuario);

            DocumentacaoSelo documentacaoSelo = documentacaoSeloService.create(
                createDocumentacaoSeloDto,
                usuario,
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
            );

            return ResponseEntity.status(HttpStatus.OK).body(documentacaoSelo);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao criar documentação");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/pendentes")
    public ResponseEntity<?> getValidacoesPendentes() {
        try {
            List<EventoDocumentacoesSeloDto> pendentesByEvento = documentacaoSeloService.getPendentesByEvento();
            List<EstabelecimentoDocumentacaoSeloDto> pendentesByEstabelecimento = documentacaoSeloService.getPendentesByEstabelecimento();
            List<Object> pendentes = new ArrayList<>();

            pendentes.addAll(pendentesByEvento);
            pendentes.addAll(pendentesByEstabelecimento);

            return ResponseEntity.status(HttpStatus.OK).body(pendentes);
        } catch (Exception e) {
            logger.error("Erro ao recuperar selos com documentações pendentes a serem validadas");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar selos com documentações pendentes a serem validadas");
        }
    }
}
