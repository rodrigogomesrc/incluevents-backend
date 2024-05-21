package br.ufrn.imd.incluevents.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.EventoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.VotacaoSelo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.service.VotacaoSeloService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("votacoes-selo")
@CrossOrigin(origins = "http://localhost:3000")
public class VotacaoSeloController {
    private final VotacaoSeloService votacaoSeloService;
    private final GetUsuarioLogadoHelper getUsuarioLogadoHelper;

    private static final Logger logger = LoggerFactory.getLogger(VotacaoSeloController.class);

    public VotacaoSeloController(VotacaoSeloService votacaoSeloService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        this.votacaoSeloService = votacaoSeloService;
        this.getUsuarioLogadoHelper = getUsuarioLogadoHelper;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateVotacaoSeloDto createVotacaoSeloDto) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            VotacaoSelo votacaoSelo = votacaoSeloService.create(createVotacaoSeloDto, usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(votacaoSelo);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao criar votação de selo", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar votação de selo");
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
                ? votacaoSeloService.getDisponiveisByEvento(idEvento, usuario)
                : votacaoSeloService.getDisponiveisByEstabelecimento(idEstabelecimento, usuario);

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

            List<EventoGrupoVotacaoSeloDto> pendentesByEvento = votacaoSeloService.getValidacoesPendentesByEvento(usuario);
            List<EstabelecimentoGrupoVotacaoSeloDto> pendentesByEstabelecimento = votacaoSeloService.getValidacoesPendentesByEstabelecimento(usuario);
            List<Object> pendentes = new ArrayList<>();

            pendentes.addAll(pendentesByEvento);
            pendentes.addAll(pendentesByEstabelecimento);

            return ResponseEntity.status(HttpStatus.OK).body(pendentes);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao recuperar selos com votações pendentes a serem validadas");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar selos com validações pendentes a serem validadas");
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validate(@RequestBody ValidateVotacaoDto validateVotacaoDto) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            votacaoSeloService.validateVotacao(validateVotacaoDto, usuario);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao validar votações");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao validar votações");
        }
    }
}
