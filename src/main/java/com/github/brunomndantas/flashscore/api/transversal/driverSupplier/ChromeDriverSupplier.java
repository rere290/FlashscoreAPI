package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import com.github.brunomndantas.jscrapper.Utils;
import com.github.brunomndantas.jscrapper.support.driverSupplier.DriverSupplier;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverSupplier extends DriverSupplier {

    protected boolean headless;
    protected boolean silent;
    protected String driverPath;

    public ChromeDriverSupplier(String driverPath, boolean silent, boolean headless) {
        this.driverPath = driverPath;
        this.silent = silent;
        this.headless = headless;
    }

    @Override
    public WebDriver getDriver() throws Exception {

        ChromeOptions options = new ChromeOptions();

        if (this.driverPath != null) {
            String path = Utils.getAbsolutePath(this.driverPath);
            System.setProperty("webdriver.chrome.driver", path);
        }

        System.setProperty(
                "webdriver.chrome.silentOutput",
                Boolean.toString(this.silent)
        );

        // Railway / Docker
        if (this.headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // Réduit la consommation mémoire
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-breakpad");
        options.addArguments("--disable-component-update");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-sync");
        options.addArguments("--disable-notifications");

        // Évite certaines fonctions inutiles
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");

        // Taille fixe
        options.addArguments("--window-size=1280,720");

        // Autorisation Selenium / ChromeDriver
        options.addArguments("--remote-allow-origins=*");

        // Réduction supplémentaire de la consommation mémoire
        options.addArguments("--disable-features=Translate,MediaRouter,BackForwardCache");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-ipc-flooding-protection");

        // User-Agent
        options.addArguments(
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/152.0.0.0 Safari/537.36"
        );

        return new ChromeDriver(options);
    }
}
