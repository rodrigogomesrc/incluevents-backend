package br.ufrn.imd.incluevents.ufpb.service;

import br.ufrn.imd.incluevents.framework.model.Evento;
import br.ufrn.imd.incluevents.framework.model.enums.OrigemEventoEnum;
import br.ufrn.imd.incluevents.framework.service.EventScraper;
import br.ufrn.imd.incluevents.framework.service.SeleniumPageRetriever;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UfpbScraperImpl implements EventScraper {

    @Value("${mineracao.url.ufpb}")
    private String url;

    private final SeleniumPageRetriever pageRetriever;

    public UfpbScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape() {
        List<Evento> eventos = scrapeList();
//        eventos.forEach(this::preencherDetalhes);
        return eventos;
    }

    private List<Evento> scrapeList() {
        ArrayList<Evento> eventos = new ArrayList<>();
        Document pagina;
        System.out.println("Scraping UFPB...");
        String html = pageRetriever.getHtmlCode(url);
        pagina = Jsoup.parse(html);

        Elements containers = pagina.select("#content-core");
        Elements articles = containers.select("article");

        articles.forEach(article -> {
            Element tituloElement = article.selectFirst(".tileHeadline");
            Element tituloElementLink = tituloElement != null ? tituloElement.selectFirst("a") : null;
            String url = tituloElementLink != null ? tituloElementLink.attr("href") : null;
            String titulo = tituloElementLink != null ? tituloElementLink.text() : null;

            Element tileContentElement = article.selectFirst(".tileContent");
            Element linkElement = tileContentElement != null ? tileContentElement.selectFirst("a") : null;
            Element imgElement = linkElement != null ? linkElement.selectFirst("img") : null;
            String imgUrl = imgElement != null ? imgElement.absUrl("src") : null;

            Evento evento = new Evento();
            evento.setImagemUrl(imgUrl);
            evento.setNome(titulo);
            evento.setUrlOriginal(url);
            evento.setOrigem(OrigemEventoEnum.UFPB);
            evento.setLocal("UFPB");
            eventos.add(evento);

        });

        return eventos;
    }

    private Evento preencherDetalhes(Evento evento) {
        this.waitOneSecond();
        String urlEvento = evento.getUrlOriginal();

        String html = pageRetriever.getHtmlCode(urlEvento);
        Document pagina = Jsoup.parse(html);

        Element pageElement = pagina.selectFirst("#parent-fieldname-text");
        Elements paragraphs = pageElement != null ? pageElement.select("p") : null;

        if (paragraphs == null) {
            return evento;
        }

        StringBuilder description = new StringBuilder();

        for (Element paragraph : paragraphs) {
            description.append(paragraph.text()).append("\n");
        }
        evento.setDescricao(description.toString());
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
