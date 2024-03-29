package br.ufrn.imd.incluevents.util;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlExtractor {
    public static String extractBaseUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle the MalformedURLException, for example, return null or throw an exception
            return null;
        }
    }

}
