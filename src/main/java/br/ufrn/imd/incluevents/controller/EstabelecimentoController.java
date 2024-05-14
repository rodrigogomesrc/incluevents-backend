package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.service.EstabelecimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/estabelecimentos")
@CrossOrigin(origins = "http://localhost:3000")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    private static final Logger logger = LoggerFactory.getLogger(EstabelecimentoController.class);

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @PostMapping
    public ResponseEntity<?> createEstabelecimento(@RequestBody Estabelecimento estabelecimento) {
        try {
            Estabelecimento createdEstabelecimento = estabelecimentoService.createEstabelecimento(estabelecimento);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEstabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e){
            logger.error("Erro ao salvar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateEstabelecimento(@RequestBody Estabelecimento estabelecimento) {
        try {
            Estabelecimento updatedEstabelecimento = estabelecimentoService.updateEstabelecimento(estabelecimento);
            return ResponseEntity.ok().body(updatedEstabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e){
            logger.error("Erro ao salvar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEstabelecimentoById(@PathVariable("id") int id) {
        try {
            Estabelecimento estabelecimento = estabelecimentoService.getEstabelecimentoById(id);

            return ResponseEntity.ok().body(estabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estabelecimento não encontrado");
        } catch (Exception e) {
            logger.error("Erro ao buscar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Estabelecimento");
        }
    }

    @PutMapping("/{estabelecimentoId}/selos/{seloId}")
    public ResponseEntity<?> addSeloToEstabelecimento (
            @PathVariable("estabelecimentoId") int estabelecimentoId,
            @PathVariable("seloId") int seloId) {
        try {
            Estabelecimento estabelecimento = estabelecimentoService.addSeloToEstabelecimento(estabelecimentoId, seloId);
            return ResponseEntity.ok().body(estabelecimento);
        }  catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao adicionar selo ao estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

    @DeleteMapping("/{estabelecimentoId}/selos/{seloId}")
    public ResponseEntity<?> removeSeloFromEstabelecimento (
            @PathVariable("estabelecimentoId") int estabelecimentoId,
            @PathVariable("seloId") int seloId) {
        try {
            Estabelecimento estabelecimento = estabelecimentoService.removeSeloFromEstabelecimento(estabelecimentoId, seloId);
            return ResponseEntity.ok().body(estabelecimento);
        }  catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao remover selo do estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstabelecimento(@PathVariable("id") int id) {
        try {
            estabelecimentoService.deleteEstabelecimento(id);
            return ResponseEntity.ok().body("Estabelecimento deletado com sucesso");
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao deletar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Estabelecimento");
        }
    }

}
