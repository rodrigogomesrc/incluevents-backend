package br.ufrn.imd.incluevents.config;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.EventoService;
import br.ufrn.imd.incluevents.service.IngresseScraperImpl;
import br.ufrn.imd.incluevents.service.OutgoScraperImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Setup implements ApplicationRunner {

    private final OutgoScraperImpl outgoScraper;
    private final IngresseScraperImpl ingresseScraper;
    private final EventoService eventoService;


    public Setup(OutgoScraperImpl outgoScraper, IngresseScraperImpl ingresseScraper, EventoService eventoService) {
        this.outgoScraper = outgoScraper;
        this.ingresseScraper = ingresseScraper;
        this.eventoService = eventoService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Outgo{
//            List<Evento> outgoEventos = outgoScraper.scrape();
//            System.out.println("Eventos OutGo buscados: ");
//            outgoEventos.forEach(System.out::println);
//            this.eventoService.saveAll(outgoEventos);
        // }

        //Ingresse{
//            List<Evento> ingresseEventos = ingresseScraper.scrape();
//            System.out.println("Eventos Ingresse buscados: ");
//            ingresseEventos.forEach(System.out::println);
//            this.eventoService.saveAll(ingresseEventos);
        //}
    }
}
