package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
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
    public Evento createEvento(@RequestBody Evento evento) {
        return service.save(evento);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findEventoById(@PathVariable Integer id) {
        try {
            Evento evento = service.getById(id);

            return ResponseEntity.status(HttpStatus.OK).body(evento);
        } catch (EventoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento não encontrado");
        }
    }

    @PutMapping("/{id}")
    public Evento updateEvento(@RequestBody Evento evento) {
        return service.update(evento);
    }

    @DeleteMapping("/{id}")
    public void deleteEvento(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
