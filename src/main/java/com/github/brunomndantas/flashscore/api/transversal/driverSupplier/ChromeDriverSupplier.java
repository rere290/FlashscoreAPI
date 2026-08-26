package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import com.github.brunomndantas.jscrapper.Utils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
             * Utilise le ChromeDriver fourni par Railway
             * uniquement si un chemin est configuré.
             */
            if (this.driverPath != null
                    && !this.driverPath.trim().isEmpty()) {

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
             * Headless pour Railway/Linux.
             */
            if (this.headless) {
                options.addArguments("--headless=new");
            }

            /*
             * Options nécessaires pour Docker/Railway.
             */
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");

            /*
             * Taille de la fenêtre.
             */
            options.addArguments("--window-size=1280,720");

            /*
             * Réduit les processus inutiles de Chrome.
             */
            options.addArguments("--no-first-run");
            options.addArguments("--no-default-browser-check");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-notifications");

            /*
             * Communication Selenium / ChromeDriver.
             */
            options.addArguments("--remote-allow-origins=*");

            /*
             * User-Agent Chrome/Linux.
             */
            options.addArguments(
                    "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/152.0.0.0 Safari/537.36"
            );

            return new ChromeDriver(options);

        } catch (Exception e) {

            throw new DriverSupplierException(
                    "Error creating ChromeDriver!",
                    e
            );
        }
    }
}
