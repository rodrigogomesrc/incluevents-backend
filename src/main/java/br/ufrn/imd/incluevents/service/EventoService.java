package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.repository.EventoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final List<EventScraper> scrapers;

    public EventoService(EventoRepository repository, List<EventScraper> scrapers) {
        this.repository = repository;
        this.scrapers = scrapers;
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

    @Scheduled(cron="0 0 5 * * ?")
    public List<Evento> scrapeAndSave() {
        List<Evento> eventosSalvos = this.findAll();
        Map<String, Evento> eventosSalvosPorUrl = eventosSalvos.stream().collect(Collectors.toMap(Evento::getUrlOriginal, evento -> evento));

        List<Evento> eventosMinerados = new ArrayList<>();
        scrapers.forEach(scraper -> {
            eventosMinerados.addAll(scraper.scrape());
        });

        eventosMinerados.forEach(evento -> {
            Evento eventoSalvo = eventosSalvosPorUrl.get(evento.getUrlOriginal());

            if (eventoSalvo != null) {
                evento.setId(eventoSalvo.getId());
            }
        });

        return this.saveAll(eventosMinerados);
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

    public List<Evento> findPaginated(Integer page, Integer pageSize) {
        Page<Evento> eventPage = repository.findAll(PageRequest.of(page - 1, pageSize));
        return eventPage.getContent();
    }
}
