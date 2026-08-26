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

    public ChromeDriverSupplier(
            String driverPath,
            boolean silent,
            boolean headless
    ) {
        this.driverPath = driverPath;
        this.silent = silent;
        this.headless = headless;
    }

    @Override
    public WebDriver getDriver() throws Exception {

        ChromeOptions options = new ChromeOptions();

        /*
         * Utilise le ChromeDriver fourni/configuré.
         * Sur Railway, driver.path peut être vide ou configuré
         * par la variable d'environnement.
         */
        if (this.driverPath != null && !this.driverPath.trim().isEmpty()) {

            String path = Utils.getAbsolutePath(this.driverPath);

            System.setProperty(
                    "webdriver.chrome.driver",
                    path
            );
        }

        System.setProperty(
                "webdriver.chrome.silentOutput",
                Boolean.toString(this.silent)
        );

        /*
         * Railway / Linux :
         * Chrome doit fonctionner en mode headless.
         */
        if (this.headless) {
            options.addArguments("--headless=new");
        }

        /*
         * Options indispensables pour un environnement Docker/Railway.
         */
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        /*
         * Évite l'utilisation de l'accélération graphique
         * qui n'est pas nécessaire sur Railway.
         */
        options.addArguments("--disable-gpu");

        /*
         * Taille de fenêtre fixe.
         */
        options.addArguments("--window-size=1280,720");

        /*
         * Évite certaines fenêtres/notifications inutiles.
         */
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");

        /*
         * Évite le premier lancement et certaines vérifications
         * inutiles de Chrome.
         */
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");

        /*
         * Autorise ChromeDriver/Selenium dans cet environnement.
         */
        options.addArguments("--remote-allow-origins=*");

        /*
         * User-Agent normal de Chrome Linux.
         */
        options.addArguments(
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/152.0.0.0 Safari/537.36"
        );

        /*
         * Réduit légèrement la consommation mémoire
         * sans désactiver massivement des composants Chrome.
         */
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-sync");

        /*
         * Création du navigateur.
         */
        return new ChromeDriver(options);
    }
}
