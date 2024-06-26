package br.ufrn.imd.incluevents.framework.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

@Service
public class SeleniumPageRetriever {

    private final WebDriver driver;

    public SeleniumPageRetriever() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);

    }

    public String getHtmlCode(String url) {
        driver.get(url);
        return driver.getPageSource();
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

}
