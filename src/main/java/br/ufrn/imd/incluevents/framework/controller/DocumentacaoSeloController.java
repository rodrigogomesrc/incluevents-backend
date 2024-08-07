package br.ufrn.imd.incluevents.framework.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.dto.CreateDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EventoDocumentacoesSeloDto;
import br.ufrn.imd.incluevents.framework.dto.ValidateDocumentacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.framework.service.DocumentacaoSeloService;

@RestController
@RequestMapping("documentacoes-selo")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentacaoSeloController {
    private final DocumentacaoSeloService documentacaoSeloService;
    private final GetUsuarioLogadoHelper getUsuarioLogadoHelper;

    private static final Logger logger = LoggerFactory.getLogger(DocumentacaoSeloController.class);

    public DocumentacaoSeloController(DocumentacaoSeloService documentacaoSeloService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        this.documentacaoSeloService = documentacaoSeloService;
        this.getUsuarioLogadoHelper = getUsuarioLogadoHelper;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

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

    @GetMapping("/disponiveis")
    public ResponseEntity<?> getTiposSeloDisponiveis(
        @RequestParam(required = false) Integer idEstabelecimento,
        @RequestParam(required = false) Integer idEvento
    ) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            List<TipoSeloEnum> tiposSelo = idEvento != null
                ? documentacaoSeloService.getDisponiveisByEvento(idEvento, usuario)
                : documentacaoSeloService.getDisponiveisByEstabelecimento(idEstabelecimento, usuario);

            return ResponseEntity.status(HttpStatus.OK).body(
                tiposSelo.stream()
                    .parallel()
                    .map(tipoSelo -> {
                        Map<String, String> object = new HashMap<>();

                        object.put("tipoSelo", tipoSelo.getTipoSelo());
                        object.put("tipoEntidade", tipoSelo.getTipoEntidade());
                        object.put("nome", tipoSelo.getNome());
                        object.put("icone", tipoSelo.getIcone());
                        object.put("descricao", tipoSelo.getDescricao());

                        return object;
                    })
                    .collect(Collectors.toList())
            );
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao recuperar tipos de selo disponíveis", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar tipos de selo disponíveis");
        }
    }

    @GetMapping("/pendentes")
    public ResponseEntity<?> getValidacoesPendentes() {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            List<EventoDocumentacoesSeloDto> pendentesByEvento = documentacaoSeloService.getValidacoesPendentesByEvento(usuario);
            List<EstabelecimentoDocumentacaoSeloDto> pendentesByEstabelecimento = documentacaoSeloService.getValidacoesPendentesByEstabelecimento(usuario);
            List<Object> pendentes = new ArrayList<>();

            pendentes.addAll(pendentesByEvento);
            pendentes.addAll(pendentesByEstabelecimento);

            return ResponseEntity.status(HttpStatus.OK).body(pendentes);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao recuperar selos com documentações pendentes a serem validadas");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar selos com documentações pendentes a serem validadas");
        }
    }

    @PostMapping("/valida")
    public ResponseEntity<?> validate(@RequestBody ValidateDocumentacaoDto validateDocumentacaoDto) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            documentacaoSeloService.validateDocumentacao(validateDocumentacaoDto, usuario);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao validar votações");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao validar documentação");
        }
    }
}
