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

import br.ufrn.imd.incluevents.dto.CreateValidacaoDto;
import br.ufrn.imd.incluevents.exceptions.EstabelecimentoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.SeloJaValidadoException;
import br.ufrn.imd.incluevents.exceptions.TipoSeloInvalidoException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.exceptions.ValidacaoJaCriadaException;
import br.ufrn.imd.incluevents.exceptions.ValidacaoNotFoundException;
import br.ufrn.imd.incluevents.model.Validacao;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.service.ValidacaoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("validacoes")
@CrossOrigin(origins = "http://localhost:3000")
public class ValidacaoController {
    private final ValidacaoService validacaoService;

    private static final Logger logger = LoggerFactory.getLogger(ValidacaoController.class);

    public ValidacaoController(ValidacaoService validacaoService) {
        this.validacaoService = validacaoService;
    }

    @PostMapping
    public ResponseEntity<?> createValidacao(@RequestBody CreateValidacaoDto createValidacaoDto) {
        try {
            if (createValidacaoDto.idEstabelecimento() == null && createValidacaoDto.idEvento() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter idEvento ou idEstabelecimento");
            } else if (createValidacaoDto.idEstabelecimento() != null && createValidacaoDto.idEvento() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter apenas idEvento ou idEstabelecimento");
            } else if (createValidacaoDto.idUsuario() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter idUsuario");
            } else if (createValidacaoDto.tipoSelo() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter tipoSelo");
            }

            Validacao validacao = validacaoService.create(createValidacaoDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(validacao);
        } catch (EventoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento não encontrado");
        } catch (EstabelecimentoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estabelecimento não encontrado");
        } catch (SeloJaValidadoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Selo já validado");
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (TipoSeloInvalidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de selo inválido");
        } catch (ValidacaoJaCriadaException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Validação já foi criada para esse selo neste evento");
        } catch (Exception e) {
            logger.error("Erro ao criar validação", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar validação");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getValidacaoById(@PathVariable("id") int id) {
        try {
            Validacao validacao = validacaoService.getById(id);

            return ResponseEntity.status(HttpStatus.OK).body(validacao);
        } catch (ValidacaoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Validação não encontrada");
        } catch (Exception e) {
            logger.error("Erro ao recuperar validação", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar validação");
        }
    }

    @GetMapping
    public ResponseEntity<?> getValidacaoByIdUsuario(@RequestParam int idUsuario) {
        try {
            List<Validacao> validacoes = validacaoService.getByUsuario(idUsuario);

            return ResponseEntity.status(HttpStatus.OK).body(validacoes);
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (Exception e) {
            logger.error("Erro ao recuperar validações", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar validações");
        }
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<?> getValidacoesTiposSeloDisponiveis(
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
                ? validacaoService.getDisponiveisByEvento(idUsuario, idEvento)
                : validacaoService.getDisponiveisByEstabelecimento(idUsuario, idEstabelecimento);

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
        } catch (EstabelecimentoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estabelecimento não encontrado");
        } catch (UsuarioNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        } catch (Exception e) {
            logger.error("Erro ao recuperar tipos de selo disponíveis", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar tipos de selo disponíveis");
        }
    }
}
