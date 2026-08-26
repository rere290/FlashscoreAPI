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
         * ============================================================
         * CHROMEDRIVER
         * ============================================================
         *
         * On utilise le chemin fourni par la configuration uniquement
         * lorsqu'il existe réellement.
         *
         * Sur Railway, ChromeDriver peut déjà être disponible dans
         * l'environnement.
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
         * ============================================================
         * MODE HEADLESS
         * ============================================================
         *
         * Railway fonctionne sans interface graphique.
         */
        if (this.headless) {
            options.addArguments("--headless=new");
        }

        /*
         * ============================================================
         * RAILWAY / DOCKER / LINUX
         * ============================================================
         */

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        /*
         * Désactivation du GPU.
         */
        options.addArguments("--disable-gpu");

        /*
         * ============================================================
         * RÉDUCTION DE LA CONSOMMATION MÉMOIRE
         * ============================================================
         *
         * Chrome peut consommer beaucoup de mémoire avec certaines
         * fonctionnalités activées par défaut.
         */

        options.addArguments("--disable-extensions");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-breakpad");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-features=Translate,MediaRouter,OptimizationHints");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-renderer-backgrounding");

        /*
         * Pas de notifications ou de vérifications inutiles.
         */
        options.addArguments("--disable-notifications");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");

        /*
         * ============================================================
         * MÉMOIRE / PROCESSUS
         * ============================================================
         *
         * Chrome utilise normalement plusieurs processus.
         * Cette option limite certaines séparations de processus.
         *
         * On évite volontairement des paramètres agressifs qui
         * pourraient provoquer des problèmes avec Flashscore.
         */
        options.addArguments("--renderer-process-limit=2");

        /*
         * ============================================================
         * FENÊTRE
         * ============================================================
         */

        options.addArguments("--window-size=1280,720");

        /*
         * ============================================================
         * SELENIUM / CHROMEDRIVER
         * ============================================================
         */

        options.addArguments("--remote-allow-origins=*");

        /*
         * ============================================================
         * USER-AGENT
         * ============================================================
         */

        options.addArguments(
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/152.0.0.0 Safari/537.36"
        );

        /*
         * ============================================================
         * CRÉATION DU NAVIGATEUR
         * ============================================================
         */

        return new ChromeDriver(options);
    }
}
