package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public List<Evento> findAll(){
        return repository.findAll();
    }
}
