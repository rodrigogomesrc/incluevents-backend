package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.EventoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "http://localhost:3000")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Evento> findAll() {
        return service.findAll();
    }

    @GetMapping("/paginated/{page}/{pageSize}")
    public List<Evento> findPaginated(@PathVariable Integer page, @PathVariable Integer pageSize) {
        return service.findPaginated(page, pageSize);
    }

    @PostMapping
    public ResponseEntity<?> createEvento(@RequestBody Evento evento) {
        try {
            Evento createdEvento = service.save(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvento);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findEventoById(@PathVariable Integer id) {
        try {
            Evento evento = service.getById(id);
            return ResponseEntity.status(HttpStatus.OK).body(evento);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/txt={nome}")
    public ResponseEntity<?> findEventoByNome(@PathVariable String nome) {
        try {
            List<Evento> eventos = service.getByName(nome);
            return ResponseEntity.status(HttpStatus.OK).body(eventos);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvento(@PathVariable Integer id, @RequestBody Evento evento) {
        evento.setId(id);
        try {
            Evento updatedEvento = service.update(evento);
            return ResponseEntity.status(HttpStatus.OK).body(updatedEvento);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable Integer id) {
        try {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
