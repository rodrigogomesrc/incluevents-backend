package br.ufrn.imd.incluevents.framework.controller;

import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.dto.CreateEstabelecimentoDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateEstabelecimentoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
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
    private final GetUsuarioLogadoHelper getUsuarioLogadoHelper;

    private static final Logger logger = LoggerFactory.getLogger(EstabelecimentoController.class);

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        this.estabelecimentoService = estabelecimentoService;
        this.getUsuarioLogadoHelper = getUsuarioLogadoHelper;
    }

    @PostMapping
    public ResponseEntity<?> createEstabelecimento(@RequestBody CreateEstabelecimentoDto estabelecimento) {
        try {
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            EstabelecimentoDto createdEstabelecimento = estabelecimentoService.createEstabelecimento(estabelecimento, usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEstabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao criar estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar estabelecimento");
        }
    }

    @PutMapping
    public ResponseEntity<?> updateEstabelecimento(@RequestBody UpdateEstabelecimentoDto estabelecimento) {
        try {
            return ResponseEntity.ok().body(estabelecimentoService.updateEstabelecimento(estabelecimento));
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao atualizar estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar estabelecimento");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEstabelecimentoById(@PathVariable("id") int id) {
        try {
            return ResponseEntity.ok().body(estabelecimentoService.getById(id));
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao buscar estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar estabelecimento");
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
            logger.error("Erro ao deletar estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar estabelecimento");
        }
    }

}
