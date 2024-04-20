package br.ufrn.imd.incluevents.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.dto.CreateValidacaoDto;
import br.ufrn.imd.incluevents.exceptions.EstabelecimentoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.SeloJaValidadoException;
import br.ufrn.imd.incluevents.model.Validacao;
import br.ufrn.imd.incluevents.service.ValidacaoService;

@RestController
@RequestMapping("validacoes")
@CrossOrigin(origins = "http://localhost:8080")
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
            } else if (createValidacaoDto.voto() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter voto");
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
        } catch (Exception e) {
            logger.error("Erro ao criar validação", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar validação");
        }
    }
}
