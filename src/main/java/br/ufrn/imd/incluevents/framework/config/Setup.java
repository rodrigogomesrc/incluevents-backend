package br.ufrn.imd.incluevents.framework.config;

//import br.ufrn.imd.incluevents.natal.service.IngresseScraperImpl;
//import br.ufrn.imd.incluevents.natal.service.OutgoScraperImpl;
//import br.ufrn.imd.incluevents.natal.service.SymplaScraperImpl;

import br.ufrn.imd.incluevents.framework.service.EventoService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Setup implements ApplicationRunner {

    private final EventoService eventoService;

    public Setup(EventoService eventoService) {
        this.eventoService = eventoService;
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
          //List<Evento> ufpbEventos = ufpbScraper.scrape();

        //System.out.println("Eventos Maracanã buscados: ");
        //List<Evento> evntos = maracanaScraper.scrape();
        //evntos.forEach(System.out::println);

//        eventoService.scrapeAndSave();
    }
}
