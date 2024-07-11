package br.ufrn.imd.incluevents.maracana.service;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class MaracanaScraperImpl implements EventScraper {

    @Value("${mineracao.url.maracana}")
    private String url;

    private static final Map<String, String> MONTH_MAP = new HashMap<>();

    static {
        MONTH_MAP.put("jan", "01");
        MONTH_MAP.put("fev", "02");
        MONTH_MAP.put("mar", "03");
        MONTH_MAP.put("abr", "04");
        MONTH_MAP.put("mai", "05");
        MONTH_MAP.put("jun", "06");
        MONTH_MAP.put("jul", "07");
        MONTH_MAP.put("ago", "08");
        MONTH_MAP.put("set", "09");
        MONTH_MAP.put("out", "10");
        MONTH_MAP.put("nov", "11");
        MONTH_MAP.put("dez", "12");
    }


    private final SeleniumPageRetriever pageRetriever;

    public MaracanaScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape() {
        return scrapeList();
    }

    private List<Evento> scrapeList() {
        ArrayList<Evento> eventos = new ArrayList<>();
        Document pagina;
        System.out.println("Scraping Macarana...");
        String html = pageRetriever.getHtmlCode(url);
        pagina = Jsoup.parse(html);

        Element gamesRow = pagina.selectFirst("#games");
        Elements rows = gamesRow != null ? gamesRow.select(".row") : null;

        Elements elements = new Elements();
        if (rows != null) {
            rows.forEach(row -> {
                if ("row".equals(row.className())) {
                    elements.add(row);
                }
            });
        }

        if (!elements.isEmpty()) {
            elements.forEach(row -> {
                Element rowInner = row.children().first();

                Element rowInner2 = null;
                if (rowInner != null) {
                    rowInner2 = rowInner.children().first();
                }

                if (rowInner2 != null && rowInner2.children().size() >= 3) {
                    Element dataElement = rowInner2.child(0);
                    Element imgElement = rowInner2.child(1);
                    Element titleElement = rowInner2.child(2);

                    String dateText = dataElement.text();
                    String[] dateParts = dateText.split(" ");
                    String day = dateParts[0];
                    String month = dateParts[1];

                    Elements imgTags = imgElement.select("img");
                    List<String> imgUrls = new ArrayList<>();
                    for (Element imgTag : imgTags) {
                        imgUrls.add(imgTag.attr("src"));
                    }

                    String title = Objects.requireNonNull(titleElement.selectFirst("h6")).text();
                    String matchup = Objects.requireNonNull(titleElement.selectFirst("h4")).text();
                    String time = Objects.requireNonNull(titleElement.selectFirst("h5")).text();

                    title = title + " - " + matchup;
                    time = day + " " + month + " " + time;

                    String urls = imgUrls.toString();

                    Evento evento = new Evento();
                    evento.setNome(title);
                    evento.setImagemUrl(urls);
                    evento.setInicio(getDate(time));
                    evento.setOrigem(OrigemEventoEnum.MARACANA);
                    eventos.add(evento);
                }
            });
        }

        return eventos;
    }

    private Date getDate(String dateText) {
        dateText = dateText.replace("h", ":");

        for (Map.Entry<String, String> entry : MONTH_MAP.entrySet()) {
            if (dateText.toLowerCase().contains(entry.getKey())) {
                dateText = dateText.toLowerCase().replace(entry.getKey(), entry.getValue());
                break;
            }
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd MM HH:mm", new Locale("pt", "BR"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy 'às' HH:mm");

        Date date = null;
        try {
            date = inputFormat.parse(dateText);
            String formattedDate = outputFormat.format(date);
            date = outputFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
