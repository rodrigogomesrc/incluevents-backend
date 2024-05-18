package br.ufrn.imd.incluevents.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.model.GrupoVotacaoSelo;
import br.ufrn.imd.incluevents.model.VotacaoSelo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.service.VotacaoSeloService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("votacoes-selo")
@CrossOrigin(origins = "http://localhost:3000")
public class VotacaoSeloController {
    private final VotacaoSeloService votacaoSeloService;

    private static final Logger logger = LoggerFactory.getLogger(VotacaoSeloController.class);

    public VotacaoSeloController(VotacaoSeloService votacaoSeloService) {
        this.votacaoSeloService = votacaoSeloService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateVotacaoSeloDto createVotacaoSeloDto) {
        try {
            if (createVotacaoSeloDto.idEstabelecimento() == null && createVotacaoSeloDto.idEvento() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter idEvento ou idEstabelecimento");
            } else if (createVotacaoSeloDto.idEstabelecimento() != null && createVotacaoSeloDto.idEvento() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter apenas idEvento ou idEstabelecimento");
            } else if (createVotacaoSeloDto.idUsuario() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter idUsuario");
            } else if (createVotacaoSeloDto.tipoSelo() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter tipoSelo");
            }

            VotacaoSelo votacaoSelo = votacaoSeloService.create(createVotacaoSeloDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(votacaoSelo);
        } catch (EventoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento não encontrado");
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao criar votação de selo", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar votação de selo");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        try {
            VotacaoSelo votacaoSelo = votacaoSeloService.getById(id);

            return ResponseEntity.status(HttpStatus.OK).body(votacaoSelo);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao recuperar votação", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar votação");
        }
    }

    @GetMapping
    public ResponseEntity<?> getByIdUsuario(@RequestParam int idUsuario) {
        try {
            List<VotacaoSelo> validacoes = votacaoSeloService.getByUsuario(idUsuario);

            return ResponseEntity.status(HttpStatus.OK).body(validacoes);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (Exception e) {
            logger.error("Erro ao recuperar votações de selo", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar votações de selo");
        }
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<?> getTiposSeloDisponiveis(
        @RequestParam int idUsuario,
        @RequestParam(required = false) Integer idEstabelecimento,
        @RequestParam(required = false) Integer idEvento
    ) {
        try {
            if (idEstabelecimento == null && idEvento == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter idEvento ou idEstabelecimento");
            } else if (idEstabelecimento != null && idEvento != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter apenas idEvento ou idEstabelecimento");
            }

            List<TipoSeloEnum> tiposSelo = idEvento != null
                ? votacaoSeloService.getDisponiveisByEvento(idUsuario, idEvento)
                : votacaoSeloService.getDisponiveisByEstabelecimento(idUsuario, idEstabelecimento);

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
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (Exception e) {
            logger.error("Erro ao recuperar tipos de selo disponíveis", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar tipos de selo disponíveis");
        }
    }

    @GetMapping("/pendentes")
    public ResponseEntity<?> getValidacoesPendentes() {
        try {
            List<GrupoVotacaoSelo> pendentes = votacaoSeloService.getValidacoesPendentes();

            return ResponseEntity.status(HttpStatus.OK).body(pendentes);
        } catch (Exception e) {
            logger.error("Erro ao recuperar selos com votações pendentes a serem validadas");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar selos com validações pendentes a serem validadas");
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validate(@RequestBody ValidateVotacaoDto validateVotacaoDto) {
        try {
            votacaoSeloService.validateVotacao(validateVotacaoDto);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao validar votações");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao validar votações");
        }
    }
}
