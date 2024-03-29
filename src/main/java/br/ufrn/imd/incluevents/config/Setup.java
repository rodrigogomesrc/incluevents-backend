package br.ufrn.imd.incluevents.config;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.service.OutgoScraperImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Setup implements ApplicationRunner {

    private final OutgoScraperImpl outgoScraper;

    public Setup(OutgoScraperImpl outgoScraper) {
        this.outgoScraper = outgoScraper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //List<Evento> eventos = outgoScraper.scrape();
        //System.out.println("Eventos buscados: ");
        //eventos.forEach(System.out::println);
    }
}
