package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Categoria;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OutgoScraperImpl implements EventScraper {

    @Value("${mineracao.url.outgo}")
    private String url;

    private final SeleniumPageRetriever pageRetriever;

    public OutgoScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape()  {
        List<Evento> listaEventos = scrapeList();
        listaEventos.forEach(this::preencherDetalhes);
        return listaEventos;
    }

    private List<Evento> scrapeList() {
        ArrayList<Evento> eventos = new ArrayList<>();
        Document pagina;
        System.out.println("Scraping outgo...");
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

            Evento evento = new Evento();
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
        System.out.println("Scraping details for " + urlFinal);
        evento.setUrlOriginal(urlFinal);

        String html = pageRetriever.getHtmlCode(urlFinal);
        Document pagina = Jsoup.parse(html);
        Element local1Element = pagina.selectFirst(".eventDetail-place-title");
        Element local2Element = pagina.selectFirst(".eventDetail-place-detail");
        String local1Text = local1Element != null ? local1Element.text() : null;
        String local2Text = local2Element != null ? local2Element.text() : null;
        String local = local1Text + " | " + local2Text;
        evento.setLocal(local);

        Elements categoriasElements = pagina.select(".eventDetail-tags > button");
        Set<Categoria> categorias = new HashSet<>();
        categoriasElements.forEach(element -> {
            Categoria categoria = new Categoria(element.text());
            categorias.add(categoria);
        });

        //get the second matching element
        Elements descricaoElement = pagina.select(".section");
        String descricao = descricaoElement.get(1).children().get(1).text();
        evento.setCategorias(categorias);
        evento.setDescricao(descricao);
        return evento;
    }

    private void waitOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
