package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.repository.EventoRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository repository;
    private final SymplaScraperImpl symplaScraper;
    private final OutgoScraperImpl outgoScraper;

    public EventoService(EventoRepository repository, SymplaScraperImpl symplaScraper, OutgoScraperImpl outgoScraper) {
        this.repository = repository;
        this.symplaScraper = symplaScraper;
        this.outgoScraper = outgoScraper;
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

    @Scheduled(cron="0 0 5 1/1 * ? *")
    public List<Evento> scrapeAndSave() {
        List<Evento> eventosSalvos = this.findAll();
        Map<String, Evento> eventosSalvosPorUrl = eventosSalvos.stream().collect(Collectors.toMap(Evento::getUrlOriginal, evento -> evento));

        List<Evento> eventosMinerados = new ArrayList<>();
        eventosMinerados.addAll(symplaScraper.scrape());
        eventosMinerados.addAll(outgoScraper.scrape());

        eventosMinerados.forEach(evento -> {
            Evento eventoSalvo = eventosSalvosPorUrl.get(evento.getUrlOriginal());

            if (eventoSalvo != null) {
                evento.setId(eventoSalvo.getId());
            }
        });

        return this.saveAll(eventosMinerados);
    }
}
