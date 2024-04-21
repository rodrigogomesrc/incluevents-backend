package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.exceptions.EstabelecimentoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.service.EstabelecimentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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
        } catch (Exception e) {
            logger.error("Erro ao salvar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEstabelecimentoById(@PathVariable("id") int id) {
        try {
            Optional<Estabelecimento> estabelecimentoOptional = estabelecimentoService.getEstabelecimentoById(id);
            if (estabelecimentoOptional.isPresent()) {
                return ResponseEntity.ok().body(estabelecimentoOptional.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estabelecimento não encontrado com o id: " + id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Estabelecimento");
        }
    }

    @PutMapping("/{estabelecimentoId}/selos/{seloId}")
    public ResponseEntity<?> addSeloToEstabelecimento(
            @PathVariable("estabelecimentoId") int estabelecimentoId,
            @PathVariable("seloId") int seloId) {
        try {
            Estabelecimento estabelecimento = estabelecimentoService.addSeloToEstabelecimento(estabelecimentoId, seloId);
            return ResponseEntity.ok().body(estabelecimento);
        } catch (EstabelecimentoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estabelecimento não encontrado com o id: " + estabelecimentoId);
        } catch (SeloNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Selo não encontrado com o id: " + seloId);
        } catch (Exception e) {
            logger.error("Erro ao adicionar selo ao estabelecimento", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Estabelecimento");
        }
    }

}
