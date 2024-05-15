package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.dto.CreateEstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.UpdateEstabelecimentoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
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
    public ResponseEntity<?> createEstabelecimento(@RequestBody CreateEstabelecimentoDto estabelecimento) {
        try {
            EstabelecimentoDto createdEstabelecimento = estabelecimentoService.createEstabelecimento(estabelecimento);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEstabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateEstabelecimento(@RequestBody UpdateEstabelecimentoDto estabelecimento) {
        try {
            EstabelecimentoDto updatedEstabelecimento = estabelecimentoService.updateEstabelecimento(estabelecimento);
            return ResponseEntity.ok().body(updatedEstabelecimento);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
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
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }
    }

    @PutMapping("/{estabelecimentoId}/selos/{seloId}")
    public ResponseEntity<?> addSeloToEstabelecimento (
            @PathVariable("estabelecimentoId") int estabelecimentoId,
            @PathVariable("seloId") int seloId) {
        try {
            EstabelecimentoDto estabelecimento = estabelecimentoService.addSeloToEstabelecimento(estabelecimentoId, seloId);
            return ResponseEntity.ok().body(estabelecimento);
        }  catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }
    }

    @DeleteMapping("/{estabelecimentoId}/selos/{seloId}")
    public ResponseEntity<?> removeSeloFromEstabelecimento (
            @PathVariable("estabelecimentoId") int estabelecimentoId,
            @PathVariable("seloId") int seloId) {
        try {
            EstabelecimentoDto estabelecimento = estabelecimentoService.removeSeloFromEstabelecimento(estabelecimentoId, seloId);
            return ResponseEntity.ok().body(estabelecimento);
        }  catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstabelecimento(@PathVariable("id") int id) {
        try {
            estabelecimentoService.deleteEstabelecimento(id);
            return ResponseEntity.ok().body("Estabelecimento deletado com sucesso");
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }
    }

}
