package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import com.github.brunomndantas.jscrapper.Utils;
import com.github.brunomndantas.jscrapper.support.driverSupplier.DriverSupplier;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

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
         * Chemin ChromeDriver.
         * Sur Railway, DRIVER_PATH peut être fourni
         * par une variable d'environnement.
         */
        if (this.driverPath != null && !this.driverPath.isBlank()) {

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
         * ==============================
         * RAILWAY / DOCKER / LINUX
         * ==============================
         */

        if (this.headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-background-networking",
                "--disable-background-timer-throttling",
                "--disable-backgrounding-occluded-windows",
                "--disable-breakpad",
                "--disable-component-update",
                "--disable-default-apps",
                "--disable-hang-monitor",
                "--disable-popup-blocking",
                "--disable-sync",
                "--disable-notifications",
                "--disable-software-rasterizer",
                "--disable-features=Translate,MediaRouter,BackForwardCache,AudioServiceOutOfProcess",
                "--disable-renderer-backgrounding",
                "--disable-component-extensions-with-background-pages",
                "--disable-client-side-phishing-detection",
                "--disable-domain-reliability",
                "--no-first-run",
                "--no-default-browser-check",
                "--window-size=1280,720",
                "--remote-allow-origins=*",
                "--user-agent=Mozilla/5.0 (X11; Linux x86_64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/152.0.0.0 Safari/537.36"
        );

        /*
         * Préférences Chrome.
         */
        options.setExperimentalOption(
                "prefs",
                Map.of(
                        "profile.default_content_setting_values.notifications", 2,
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false
                )
        );

        return new ChromeDriver(options);
    }
}
