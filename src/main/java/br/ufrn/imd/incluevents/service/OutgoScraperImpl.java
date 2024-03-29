package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.enums.OrigemEventoEnum;
import br.ufrn.imd.incluevents.util.UrlExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OutgoScraperImpl implements EventScraper {

    @Value("${mineracao.url.outgo}")
    private String url;

    private ArrayList<Evento> eventos;

    private SeleniumPageRetriever pageRetriever;

    public OutgoScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.eventos = new ArrayList<>();
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape()  {
        List<Evento> listaEventos = scrapeList();
        listaEventos.forEach(this::preencherDetalhes);
        return listaEventos;
    }

    private List<Evento> scrapeList() {
        Document pagina;
        System.out.println("Scraping outgo...");
        System.out.println(url);
        String html = pageRetriever.getHtmlCode(url);


        pagina = Jsoup.parse(html);
        Elements elementos = pagina.select("card-event");

        elementos.forEach(elemento -> {

            Element imgUrlElement = elemento.select("img").first();
            Element linkContainer = elemento.selectFirst("a");
            Element informacoes = elemento.select(".cardEvent-info").first();

            String link = linkContainer != null ? linkContainer.attr("href") : null;
            String imgUrl = imgUrlElement != null ? imgUrlElement.absUrl("src") : null;
            String titulo = informacoes != null ? informacoes.select(".cardEvent-title").text() : null;
            String local = informacoes != null ? informacoes.select(".cardEvent-place").text() : null;

            Evento evento = new Evento();
            evento.setLocal(local);
            evento.setImagemUrl(imgUrl);
            evento.setNome(titulo);
            evento.setUrlOriginal(link);
            evento.setOrigem(OrigemEventoEnum.OUTGO);
            eventos.add(evento);
        });
        return eventos;
    }

    private Evento preencherDetalhes(Evento evento) {
        this.waitOneSecond();
        String urlEvento = evento.getUrlOriginal();
        String urlFinal = UrlExtractor.extractBaseUrl(url) + urlEvento;
        evento.setUrlOriginal(urlFinal);

        //Document pagina;
        //String html = pageRetriever.getHtmlCode(url);


        return null;
    }

    private void waitOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
