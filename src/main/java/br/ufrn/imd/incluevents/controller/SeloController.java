package br.ufrn.imd.incluevents.controller;


import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.service.SeloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/selos")
public class SeloController {

    @Autowired
    private SeloService seloService;

    @PostMapping
    public ResponseEntity<Selo> createSelo(@RequestBody Selo selo) {
        return seloService.save(selo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Selo> updateSelo(@RequestBody Selo selo) {
        return seloService.update(selo);
    }

    @DeleteMapping("/{id}")
    public void deleteSelo(@PathVariable Integer id) {
        seloService.deleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Selo> findSeloById(@PathVariable Integer id) {
        return seloService.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<Selo>> findAll() {
        return seloService.findAll();
    }
}
