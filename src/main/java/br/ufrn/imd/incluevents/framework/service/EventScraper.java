package br.ufrn.imd.incluevents.framework.service;

import br.ufrn.imd.incluevents.framework.model.Evento;

import java.util.List;

public interface EventScraper {
    List<Evento> scrape();
}
