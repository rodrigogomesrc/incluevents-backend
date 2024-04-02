package br.ufrn.imd.incluevents.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.enums.OrigemEventoEnum;

@Service
public class SymplaScraperImpl implements EventScraper {

    @Value("${mineracao.url.sympla}")
    private String url;

    private final SeleniumPageRetriever pageRetriever;

    public SymplaScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape() {
        List<Evento> listaEventos = scrapeList();

        return listaEventos;
    }

    public List<Evento> scrapeList() {
        ArrayList<Evento> eventos = new ArrayList<>();

        Elements elementos;
        int numeroPagina = 1;

        do {
            String url = this.url + "?page=" + numeroPagina;
            String html = pageRetriever.getHtmlCode(url);

            Document pagina = Jsoup.parse(html);

            elementos = pagina.select(".CustomGridstyle__CustomGridCardType-sc-1ce1n9e-2");

            elementos.forEach(elemento -> {
                Element linkElement = elemento.selectFirst("a");
                Element imgElement = elemento.selectFirst("img");
                Element titleElement = elemento.selectFirst(".EventCardstyle__EventTitle-sc-1rkzctc-7");
                Element addressElement = elemento.selectFirst(".EventCardstyle__EventLocation-sc-1rkzctc-8");
                Elements dates = elemento.select(".fZlvlB");

                String link = linkElement != null ? linkElement.attr("href") : null;
                String imgUrl = imgElement != null ? imgElement.attr("src") : null;
                String nome = titleElement != null ? titleElement.text() : null;
                String local = addressElement != null ? addressElement.text() : null;
                Date dataInicio = dates.size() > 0 ? parseDate(dates.get(0).text()) : null;
                Date dataFim = dates.size() > 1 ? parseDate(dates.get(1).text()) : null;

                Evento evento = new Evento();

                evento.setOrigem(OrigemEventoEnum.SYMPLA);
                evento.setUrlOriginal(link);
                evento.setImagemUrl(imgUrl);
                evento.setNome(nome);
                evento.setLocal(local);
                evento.setInicio(dataInicio);
                evento.setFim(dataFim);

                eventos.add(evento);
            });

            numeroPagina++;
        } while (elementos.size() > 0);

        return eventos;
    }

    private Date parseDate(String date) {
        if (date == null) {
            return null;
        }

        try {
            String[] dateParts = date.split(" ");

            int day, month;
            Integer hour = null, minute = null;

            // 26 Out
            if (dateParts.length == 2) {
                day = Integer.parseInt(dateParts[0]);
                month = getMonth(dateParts[1]);
            }
            // Sab, 06 Abr · 21:00
            else if (dateParts.length == 5) {
                day = Integer.parseInt(dateParts[1]);
                month = getMonth(dateParts[2]);

                String time = dateParts[4];
                String[] timeParts = time.split(":");

                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            } else {
                return null;
            }

            LocalDate currenDate = LocalDate.now();
            LocalDate localDate = LocalDate.of(currenDate.getYear(), month, day);
            LocalTime localTime = LocalTime.of(0, 0);

            if (hour != null && minute != null) {
                localTime = LocalTime.of(hour, minute);
            }

            if (localDate.isBefore(currenDate)) {
                localDate = localDate.plusYears(1);
            }

            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (NumberFormatException | DateTimeException e) {
            return null;
        }
    }

    private int getMonth(String month) {
        if (month == null) {
            return 1;
        }

        switch (month.toUpperCase()) {
            case "JAN":
                return 1;
            case "FEV":
                return 2;
            case "MAR":
                return 3;
            case "ABR":
                return 4;
            case "MAI":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AGO":
                return 8;
            case "SET":
                return 9;
            case "OUT":
                return 10;
            case "NOV":
                return 11;
            case "DEZ":
                return 12;
            default:
                return 1;
        }
    }
}
