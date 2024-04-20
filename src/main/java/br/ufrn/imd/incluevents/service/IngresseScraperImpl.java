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

import java.util.*;

@Service
public class IngresseScraperImpl implements EventScraper {

    @Value("${mineracao.url.ingresse}")
    private String url;

    private final SeleniumPageRetriever pageRetriever;

    public IngresseScraperImpl(SeleniumPageRetriever pageRetriever) {
        this.pageRetriever = pageRetriever;
    }

    @Override
    public List<Evento> scrape()  {
        List<Evento> listaEventos = scrapeList();
        return listaEventos;
    }

    private List<Evento> scrapeList() {
        ArrayList<Evento> eventos = new ArrayList<Evento>();
        ArrayList<String> linkEventos = new ArrayList<String>();
        Document pagina;
        System.out.println("Scraping ingresse...");
        String html = pageRetriever.getHtmlCode(url);
        pagina = Jsoup.parse(html);
        Element grid = pagina.getElementsByClass("grid-cols-1").first();

        if(grid != null){
            Elements cardDivs = grid.children().select("div");

            cardDivs.forEach(div -> {
                if(!div.getElementsByTag("a").attr("href").equals("")){
                    linkEventos.add("https://www.ingresse.com"+div.getElementsByTag("a").attr("href"));
                }
            });

            linkEventos.forEach(link -> {
                if(link.equals("https://www.ingresse.com/devteam-real-do-real/")){
                    return;
                }
                String segundoHtml;
                Document segundaPagina;

                segundoHtml = pageRetriever.getHtmlCode(link);
                segundaPagina = Jsoup.parse(segundoHtml);

                int loadingCount = 0;
                System.out.print("Loading.");
                while(segundaPagina.getElementsByClass("ml-2 text-white").isEmpty()) {
                    System.out.print(".");
                    this.waitSeconds(2);
                    loadingCount++;
                    if(loadingCount > 4){
                        System.out.println("\nReloading...");
                        segundoHtml = pageRetriever.getHtmlCode(link);
                        segundaPagina = Jsoup.parse(segundoHtml);
                        loadingCount = 0;
                        System.out.print("Loading.");
                    }
                }

                String titulo = segundaPagina.getElementsByAttributeValue("property", "og:title").first().attr("content");

                Elements subtitulos = segundaPagina.getElementsByClass("ml-2 text-white");
                String local = (!subtitulos.isEmpty()) ? subtitulos.get(1).text() : "";

                Element imgUrlElement = segundaPagina.getElementsByClass("flex items-center justify-end h-full").first();
                String imgUrl = (imgUrlElement != null ? imgUrlElement.getElementsByTag("img").attr("src") : null);
                Elements secoes = segundaPagina.getElementsByClass("styles__motion__D_mdN");

                String descricao = secoes.size() > 2 ? secoes.get(2).html() : "";

                Elements datas = segundaPagina.getElementsByClass("slick-slide");

                datas.forEach(data -> {
                    if(!data.attr("class").contains("slick-cloned")){
                        Date dataEvento = convertStringToDate(data.text());

                        Evento evento = new Evento();
                        evento.setImagemUrl(imgUrl);
                        evento.setNome(titulo);
                        evento.setUrlOriginal(link);
                        evento.setDescricao(truncateString(descricao));
                        evento.setLocal(local);
                        evento.setInicio(dataEvento);
                        evento.setFim(null);
                        evento.setOrigem(OrigemEventoEnum.INGRESSE);
                        eventos.add(evento);
                    }
                });
            });
        }
        return eventos;
    }

    private void waitSeconds(int seconds) {
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Date convertStringToDate(String dataString) {

        String[] dataArray = dataString.split(" "); //Padrao: [diaSemana, diaMês, mês, hora]
        Date data = new Date();

        int numeroMes = obterNumeroMes(dataArray[2]);

        Calendar cal = Calendar.getInstance();
        if(numeroMes > cal.get(Calendar.MONTH) || (numeroMes == cal.get(Calendar.MONTH) && Integer.parseInt(dataArray[1]) > cal.get(Calendar.DAY_OF_MONTH))) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
        }else {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+1);
        }


        cal.set(Calendar.MONTH, numeroMes);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[1]));

        if(dataArray[3].contains("h")) {
            dataArray[3] = dataArray[3].substring(0, dataArray[3].length()-1);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataArray[3]));
            cal.set(Calendar.MINUTE, 0);
        }else if(dataArray[3].matches("(.*):(.*)")){
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataArray[3].split(":")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(dataArray[3].split(":")[1]));
        }
        cal.set(Calendar.SECOND, 0);
        data = cal.getTime();

        return data;
    }

    public static int obterNumeroMes(String mes) {
        HashMap<String, Integer> months = new HashMap<String, Integer>();

        months.put("jan", 0);
        months.put("fev", 1);
        months.put("mar", 2);
        months.put("abr", 3);
        months.put("mai", 4);
        months.put("jun", 5);
        months.put("jul", 6);
        months.put("ago", 7);
        months.put("set", 8);
        months.put("out", 9);
        months.put("nov", 10);
        months.put("dez", 11);

        return months.get(mes.toLowerCase()) != null ? months.get(mes.toLowerCase()) : 0;
    }

    public static String truncateString(String texto) {
        if (texto.length() > 3000){
            return texto.substring(0, 2996) + "...";
        }
        return texto;
    }


}
