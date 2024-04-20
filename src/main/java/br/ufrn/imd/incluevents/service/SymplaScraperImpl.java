package br.ufrn.imd.incluevents.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.imd.incluevents.model.Estabelecimento;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.model.Categoria;
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
        System.out.println("Scraping sympla...");
        HashMap<String, Evento> eventosPorUrl = new HashMap<>();

        this.scrapeList(eventosPorUrl);

        this.scrapeCategory("Gastronomia", "1-gastronomia", eventosPorUrl);
        this.scrapeCategory("Festas e shows", "17-festas-e-shows", eventosPorUrl);
        this.scrapeCategory("Cursos e Workshops", "8-curso-e-workshops", eventosPorUrl);
        this.scrapeCategory("Congressos e Palestras", "4-congressos-e-palestras", eventosPorUrl);
        this.scrapeCategory("Arte, Cinema e Lazer", "10-arte-cinema-e-lazer", eventosPorUrl);
        this.scrapeCategory("Esportes", "2-esportes", eventosPorUrl);
        this.scrapeCategory("Saúde e Bem-Estar", "9-saude-e-bem-estar", eventosPorUrl);

        return new ArrayList<>(eventosPorUrl.values());
    }

    private void scrapeList(Map<String, Evento> eventosPorUrl) {
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
                Date dataInicio = !dates.isEmpty() ? parseDate(dates.get(0).text()) : null;
                Date dataFim = dates.size() > 1 ? parseDate(dates.get(1).text()) : null;

                String textoEstabelecimento = "";
                String textoEndereco = "";
                if (local != null){
                    textoEstabelecimento = local.split(" - ")[0];
                    textoEndereco = local.split(" - ")[1];
                }
                Evento evento;

                if (eventosPorUrl.containsKey(link)) {
                    evento = eventosPorUrl.get(link);
                } else {
                    evento = new Evento();
                    eventosPorUrl.put(link, evento);
                }

                evento.setOrigem(OrigemEventoEnum.SYMPLA);
                evento.setUrlOriginal(link);
                evento.setImagemUrl(imgUrl);
                evento.setNome(nome);
                evento.setLocal(local);
                evento.setEstabelecimento(new Estabelecimento(textoEstabelecimento, textoEndereco, null));
                evento.setInicio(dataInicio);
                evento.setFim(dataFim);
            });

            numeroPagina++;
        } while (elementos.size() > 0);
    }

    private void scrapeCategory(String nomeCategoria, String idCategoria, Map<String, Evento> eventosPorUrl) {
        this.waitOneSecond();

        Categoria categoria = new Categoria(nomeCategoria);

        Elements elementos;
        int numeroPagina = 1;

        do {
            String url = this.url + "?page=" + numeroPagina + "&cl=" + idCategoria;
            String html = pageRetriever.getHtmlCode(url);

            Document pagina = Jsoup.parse(html);

            elementos = pagina.select(".CustomGridstyle__CustomGridCardType-sc-1ce1n9e-2");

            elementos.forEach(elemento -> {
                Element linkElement = elemento.selectFirst("a");

                String link = linkElement != null ? linkElement.attr("href") : null;
                Evento evento = eventosPorUrl.get(link);

                if (evento != null) {
                    Set<Categoria> categorias = evento.getCategorias();

                    if (categorias == null) {
                        categorias = new HashSet<>();
                        evento.setCategorias(categorias);
                    }

                    categorias.add(categoria);
                }
            });

            numeroPagina++;
        } while (elementos.size() > 0);
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

    private void waitOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
