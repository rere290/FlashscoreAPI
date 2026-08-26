package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class ChromeDriverSupplier implements IDriverSupplier {

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
    public WebDriver get() throws DriverSupplierException {

        try {

            ChromeOptions options = new ChromeOptions();

            /*
             * ChromeDriver personnalisé si un chemin est fourni.
             */
            if (this.driverPath != null
                    && !this.driverPath.trim().isEmpty()) {

                File driverFile = new File(this.driverPath);

                if (driverFile.exists()) {
                    System.setProperty(
                            "webdriver.chrome.driver",
                            driverFile.getAbsolutePath()
                    );
                }
            }

            /*
             * Réduit les logs ChromeDriver.
             */
            System.setProperty(
                    "webdriver.chrome.silentOutput",
                    Boolean.toString(this.silent)
            );

            /*
             * Headless obligatoire sur Railway/Linux.
             */
            if (this.headless) {
                options.addArguments("--headless=new");
            }

            /*
             * Options Docker / Railway.
             */
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-setuid-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            /*
             * Réduit la consommation de ressources.
             */
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");

            /*
             * Empêche Chrome de lancer certaines fonctions
             * inutiles pour le scraping.
             */
            options.addArguments("--no-first-run");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            /*
             * Désactive l'accélération graphique sans
             * désactiver complètement le moteur de rendu.
             */
            options.addArguments("--disable-software-rasterizer");

            /*
             * Taille du navigateur.
             */
            options.addArguments("--window-size=1280,720");

            /*
             * Communication Selenium / ChromeDriver.
             */
            options.addArguments("--remote-allow-origins=*");

            /*
             * Réduit certains processus Chrome en arrière-plan.
             */
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-renderer-backgrounding");

            /*
             * User-Agent.
             */
            options.addArguments(
                    "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/152.0.0.0 Safari/537.36"
            );

            /*
             * Création de Chrome.
             */
            return new ChromeDriver(options);

        } catch (Exception e) {

            throw new DriverSupplierException(
                    "Error creating ChromeDriver!",
                    e
            );
        }
    }
}
