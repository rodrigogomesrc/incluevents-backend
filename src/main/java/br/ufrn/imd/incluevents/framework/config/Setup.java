package br.ufrn.imd.incluevents.framework.config;

import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.natal.service.IngresseScraperImpl;
import br.ufrn.imd.incluevents.natal.service.OutgoScraperImpl;
import br.ufrn.imd.incluevents.natal.service.SymplaScraperImpl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Setup implements ApplicationRunner {

    private final OutgoScraperImpl outgoScraper;
    private final SymplaScraperImpl SymplaScraperImpl;
    private final IngresseScraperImpl ingresseScraper;
    private final EventoService eventoService;


    public Setup(OutgoScraperImpl outgoScraper, EventoService eventoService, SymplaScraperImpl SymplaScraperImpl, IngresseScraperImpl ingresseScraper) {
        this.outgoScraper = outgoScraper;
        this.eventoService = eventoService;
        this.SymplaScraperImpl = SymplaScraperImpl;
        this.ingresseScraper = ingresseScraper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        List<Evento> eventos = outgoScraper.scrape();
//        System.out.println("Eventos buscados: ");
//        eventos.forEach(System.out::println);
//        this.eventoService.saveAll(eventos);
//
//        List<Evento> eventosSimpla = SymplaScraperImpl.scrape();
//        System.out.println("Eventos buscados: ");
//        eventosSimpla.forEach(System.out::println);
//        this.eventoService.saveAll(eventosSimpla);
//
//        List<Evento> ingresseEventos = ingresseScraper.scrape();
//        System.out.println("Eventos Ingresse buscados: ");
//        ingresseEventos.forEach(System.out::println);
//        this.eventoService.saveAll(ingresseEventos);
    }
}
