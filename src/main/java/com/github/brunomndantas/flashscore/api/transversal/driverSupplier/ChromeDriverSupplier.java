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

        /*
         * IMPORTANT POUR RAILWAY
         */
        if (this.headless) {
            options.addArguments("--headless=new");
        }

        /*
         * Évite maximize() dans un environnement Linux sans interface graphique.
         */
        options.addArguments("--window-size=1920,1080");

        /*
         * Stabilité Docker / Railway
         */
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        /*
         * Réduit fortement ce que Chrome charge inutilement.
         */
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-breakpad");
        options.addArguments("--disable-component-update");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-features=Translate,MediaRouter");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-prompt-on-repost");
        options.addArguments("--disable-sync");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");

        /*
         * Flashscore n'a pas besoin de notifications.
         */
        options.addArguments("--disable-notifications");

        /*
         * Évite certaines consommations mémoire inutiles.
         */
        options.addArguments("--disable-software-rasterizer");

        /*
         * Autorisation Selenium / ChromeDriver.
         */
        options.addArguments("--remote-allow-origins=*");

        /*
         * User agent classique.
         */
        options.addArguments(
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/152.0.0.0 Safari/537.36"
        );

        return new ChromeDriver(options);
    }
}
