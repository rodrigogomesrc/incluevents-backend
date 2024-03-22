package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.EventoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

   private EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Evento> findAll(){
        return service.findAll();
    }


}
