package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Evento;

import java.util.List;

public interface EventScraper {
    List<Evento> scrape();
}
