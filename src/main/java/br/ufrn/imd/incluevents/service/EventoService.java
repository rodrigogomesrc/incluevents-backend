package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
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

    public Evento save(Evento evento) throws BusinessException {
        if (evento.getNome().isEmpty() || evento.getNome().equals(" ")) {
            throw new BusinessException("Nome do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if (evento.getLocal().isEmpty() || evento.getLocal().equals(" ")) {
            throw new BusinessException("Local do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        return repository.save(evento);
    }

    public List<Evento> saveAll(List<Evento> eventos) throws BusinessException {
        for (Evento evento : eventos) {
            if (evento.getNome().isEmpty() || evento.getNome().equals(" ")) {
                throw new BusinessException("Nome do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
            }
            if (evento.getLocal().isEmpty() || evento.getLocal().equals(" ")) {
                throw new BusinessException("Local do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
            }
        }
        return repository.saveAll(eventos);
    }

    @Scheduled(cron="0 0 5 * * ?")
    public List<Evento> scrapeAndSave() throws BusinessException {
        List<Evento> eventosMinerados = new ArrayList<>();

        scrapers.forEach(scraper -> {
            eventosMinerados.addAll(scraper.scrape());
        });

        List<String> scrapedUrls = eventosMinerados.stream().map(Evento::getUrlOriginal).collect(Collectors.toList());
        Map<String, Evento> eventosSalvosPorUrl = this.findByUrlsOriginals(scrapedUrls)
                .stream().collect(Collectors.toMap(Evento::getUrlOriginal, evento -> evento));

        eventosMinerados.forEach(evento -> {
            Evento eventoSalvo = eventosSalvosPorUrl.get(evento.getUrlOriginal());
            if (eventoSalvo != null) {
                evento.setId(eventoSalvo.getId());
            }
        });

        return this.saveAll(eventosMinerados);
    }

    public Evento getById(Integer id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if(repository.findById(id).isEmpty()){
            throw new BusinessException("Evento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
        return repository.findById(id).get();
    }

    public Evento update(Evento evento) throws BusinessException {
        if (evento.getNome().isEmpty() || evento.getNome().equals(" ")) {
            throw new BusinessException("Nome do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if (evento.getLocal().isEmpty() || evento.getLocal().equals(" ")) {
            throw new BusinessException("Local do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if (!repository.existsById(evento.getId())) {
            throw new BusinessException("Evento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
        return repository.save(evento);
    }

    public void deleteById(Integer id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id do evento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        repository.deleteById(id);
    }

    public List<Evento> findPaginated(Integer page, Integer pageSize) {
        Page<Evento> eventPage = repository.findAll(PageRequest.of(page - 1, pageSize));
        return eventPage.getContent();
    }

    public List<Evento> findByUrlsOriginals(List<String> urls) {
        return repository. findByUrlOriginals(urls);
    }
}
