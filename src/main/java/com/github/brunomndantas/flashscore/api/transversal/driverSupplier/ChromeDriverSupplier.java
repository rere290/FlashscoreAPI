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
             * Utilisation du ChromeDriver configuré
             * dans driver.path s'il existe.
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
             * Mode headless pour Railway / Docker / Linux.
             */
            if (this.headless) {
                options.addArguments("--headless=new");
            }

            /*
             * IMPORTANT POUR RAILWAY / DOCKER
             */
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-setuid-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            /*
             * Réduit l'utilisation de ressources.
             */
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-background-networking");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");

            /*
             * Évite certaines fonctions Chrome
             * inutiles sur un serveur.
             */
            options.addArguments("--no-first-run");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            /*
             * Taille fixe pour le scraping.
             */
            options.addArguments("--window-size=1280,720");

            /*
             * Évite certains problèmes liés à la mémoire
             * graphique dans les conteneurs Linux.
             */
            options.addArguments("--disable-features=UseOzonePlatform");
            options.addArguments("--disable-features=VizDisplayCompositor");

            /*
             * Communication Selenium / ChromeDriver.
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
