package br.ufrn.imd.incluevents.controller;


import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.service.SeloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/selos")
@CrossOrigin(origins = "http://localhost:3000")
public class SeloController {

    private final SeloService seloService;

    private static final Logger logger = LoggerFactory.getLogger(SeloController.class);

    public SeloController(SeloService seloService) {
        this.seloService = seloService;
    }

    @PostMapping
    public ResponseEntity<?> createSelo(@RequestBody Selo selo) {
        try {
            Selo createdSelo = seloService.save(selo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSelo);
        } catch (Exception e) {
            logger.error("Erro ao salvar Selo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Selo");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findSeloById(@PathVariable Integer id) {
        try {
            Selo selo = seloService.getById(id);
            return ResponseEntity.ok().body(selo);
        } catch (SeloNotFoundException e) {
            logger.error("Selo não encontrado com o id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Selo não encontrado com o id: " + id);
        } catch (Exception e) {
            logger.error("Erro ao buscar Selo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Selo");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSelo(@PathVariable Integer id, @RequestBody Selo selo) {
        selo.setId(id);
        try {
            Selo updatedSelo = seloService.update(selo);
            return ResponseEntity.ok().body(updatedSelo);
        } catch (SeloNotFoundException e) {
            logger.error("Selo não encontrado com o id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Selo não encontrado com o id: " + id);
        } catch (Exception e) {
            logger.error("Erro ao atualizar Selo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar Selo");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSelo(@PathVariable Integer id) {
        try {
            seloService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar Selo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar Selo");
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Selo> selos = seloService.findAll();
            return ResponseEntity.ok().body(selos);
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os Selos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar todos os Selos");
        }
    }
}
