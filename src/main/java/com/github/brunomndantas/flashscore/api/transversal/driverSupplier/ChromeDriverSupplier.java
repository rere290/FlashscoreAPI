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

        if (this.driverPath != null && !this.driverPath.isBlank()) {
            String path = Utils.getAbsolutePath(this.driverPath);
            System.setProperty("webdriver.chrome.driver", path);
        }

        System.setProperty(
                "webdriver.chrome.silentOutput",
                Boolean.toString(this.silent)
        );

        /*
         * Configuration spéciale pour Linux / Docker / Render.
         */
        if (this.headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        /*
         * Réduit fortement l'utilisation mémoire de Chrome.
         */
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-breakpad");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-features=Translate,BackForwardCache");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-renderer-backgrounding");

        /*
         * Évite certaines erreurs liées au sandbox / GPU
         * dans les environnements conteneurisés.
         */
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-setuid-sandbox");

        /*
         * Taille de fenêtre fixe.
         * On évite maximize() dans un conteneur Linux.
         */
        options.addArguments("--window-size=1920,1080");

        /*
         * Permet à Chrome de fonctionner correctement
         * derrière un serveur distant.
         */
        options.addArguments("--remote-allow-origins=*");

        /*
         * Évite que Chrome attende inutilement certaines
         * fonctions réseau.
         */
        options.addArguments("--disable-features=NetworkServiceInProcess");

        /*
         * User-Agent classique Chrome.
         */
        options.addArguments(
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/152.0.0.0 Safari/537.36"
        );

        return new ChromeDriver(options);
    }
}
