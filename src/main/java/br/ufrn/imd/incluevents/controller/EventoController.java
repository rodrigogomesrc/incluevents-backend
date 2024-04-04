package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.EventoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Evento> findAll() {
        return service.findAll();
    }

    @PostMapping
    public Evento createEvento(@RequestBody Evento evento) {
        return service.save(evento);
    }

    @GetMapping("find/{id}")
    public Evento findEventoById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PutMapping("update/{id}")
    public Evento updateEvento(@RequestBody Evento evento) {
        return service.update(evento);
    }

    @DeleteMapping("delete/{id}")
    public void deleteEvento(@PathVariable Integer id) {
        service.deleteById(id);
    }
}