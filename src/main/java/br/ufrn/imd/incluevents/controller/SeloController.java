package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
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
        } catch (BusinessException e) {
            logger.error("Erro ao criar selo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findSeloById(@PathVariable Integer id) {
        try {
            Selo selo = seloService.getById(id);
            return ResponseEntity.ok().body(selo);
        } catch (BusinessException e) {
            logger.error("Erro ao buscar selo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSelo(@PathVariable Integer id, @RequestBody Selo selo) {
        selo.setId(id);
        try {
            Selo updatedSelo = seloService.update(selo);
            return ResponseEntity.ok().body(updatedSelo);
        } catch (BusinessException e) {
            logger.error("Erro ao atualizar selo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSelo(@PathVariable Integer id) {
        try {
            seloService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            logger.error("Erro ao deletar selo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Selo> selos = seloService.findAll();
            return ResponseEntity.ok().body(selos);
        } catch (BusinessException e) {
            logger.error("Erro ao buscar selos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
