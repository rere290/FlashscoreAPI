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
             * Réduction des logs ChromeDriver.
             */
            System.setProperty(
                    "webdriver.chrome.silentOutput",
                    Boolean.toString(this.silent)
            );

            /*
             * HEADLESS
             */
            if (this.headless) {
                options.addArguments("--headless=new");
            }

            /*
             * IMPORTANT RAILWAY / DOCKER
             */
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-setuid-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            /*
             * Stabilité Chrome dans un conteneur.
             */
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-extensions");

            /*
             * Réduit fortement les processus et services
             * inutiles pour le scraping.
             */
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
            options.addArguments("--disable-component-update");
            options.addArguments("--disable-domain-reliability");
            options.addArguments("--disable-sync");

            /*
             * Désactive les fonctions graphiques pouvant
             * provoquer des crashs dans Docker.
             */
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--disable-features=UseOzonePlatform");

            /*
             * Pas de première configuration Chrome.
             */
            options.addArguments("--no-first-run");
            options.addArguments("--no-default-browser-check");

            /*
             * Notifications / popups.
             */
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            /*
             * Taille fixe.
             */
            options.addArguments("--window-size=1280,720");

            /*
             * Mémoire.
             */
            options.addArguments("--js-flags=--max-old-space-size=512");

            /*
             * Autorisation Selenium / ChromeDriver.
             */
            options.addArguments("--remote-allow-origins=*");

            /*
             * User-Agent.
             */
            options.addArguments(
                    "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/152.0.0.0 Safari/537.36"
            );

            /*
             * Création du navigateur.
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
