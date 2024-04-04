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

    public Evento save(Evento evento){
        return repository.save(evento);
    }

    public List<Evento> saveAll(List<Evento> eventos){
        return repository.saveAll(eventos);
    }

    public Evento getById(Integer id){
        return repository.getById(id);
    }

    public Evento update(Evento evento) {
        if (repository.existsById(evento.getId())) {
            return repository.save(evento);
        }
        return null;
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
