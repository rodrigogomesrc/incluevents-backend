package br.ufrn.imd.incluevents.config;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.EventoService;
import br.ufrn.imd.incluevents.service.OutgoScraperImpl;
import br.ufrn.imd.incluevents.service.SymplaScraperImpl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Setup implements ApplicationRunner {

    private final OutgoScraperImpl outgoScraper;
    private final SymplaScraperImpl SymplaScraperImpl;
    private final EventoService eventoService;


    public Setup(OutgoScraperImpl outgoScraper, EventoService eventoService, SymplaScraperImpl SymplaScraperImpl) {
        this.outgoScraper = outgoScraper;
        this.eventoService = eventoService;
        this.SymplaScraperImpl = SymplaScraperImpl;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Evento> eventos = outgoScraper.scrape();
        System.out.println("Eventos buscados: ");
        eventos.forEach(System.out::println);
        this.eventoService.saveAll(eventos);

        List<Evento> eventos2 = SymplaScraperImpl.scrape();
        System.out.println("Eventos buscados: ");
        eventos2.forEach(System.out::println);
        this.eventoService.saveAll(eventos2);
    }
}
