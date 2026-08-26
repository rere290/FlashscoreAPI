package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import com.github.brunomndantas.flashscore.api.dataAccess.utils.FlashscoreSelectors;
import com.github.brunomndantas.flashscore.api.dataAccess.utils.FlashscoreURLs;
import com.github.brunomndantas.flashscore.api.transversal.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FlashscoreDriverSupplier implements IDriverSupplier {

    protected IDriverSupplier sourceSupplier;

    public FlashscoreDriverSupplier(IDriverSupplier sourceSupplier) {
        this.sourceSupplier = sourceSupplier;
    }

    @Override
    public WebDriver get() throws DriverSupplierException {

        WebDriver driver = null;

        try {

            /*
             * Création de ChromeDriver.
             */
            driver = sourceSupplier.get();

            /*
             * Timeout de chargement raisonnable pour Railway.
             */
            driver.manage().timeouts().pageLoadTimeout(
                    Duration.ofSeconds(60)
            );

            /*
             * Ouverture de Flashscore.
             */
            driver.get(FlashscoreURLs.FLASHSCORE_URL);

            /*
             * Petit délai pour laisser Flashscore initialiser
             * son interface JavaScript.
             */
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            /*
             * Les conditions/cookies ne doivent PAS faire
             * échouer complètement le démarrage si le bouton
             * n'est pas présent.
             */
            try {
                acceptTerms(driver);
            } catch (Exception ignored) {
                /*
                 * Flashscore peut parfois ne pas afficher
                 * le bouton ou l'avoir déjà accepté.
                 *
                 * On continue donc normalement.
                 */
            }

            return driver;

        } catch (Exception e) {

            /*
             * Fermeture propre de Chrome en cas d'erreur.
             */
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }

            throw new DriverSupplierException(
                    "Error initializing Flashscore driver!",
                    e
            );
        }
    }

    protected void acceptTerms(WebDriver driver) {

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofMillis(Config.MEDIUM_WAIT)
        );

        WebElement acceptTermsButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                FlashscoreSelectors.ACCEPT_TERMS_BUTTON_SELECTOR
                        )
                );

        try {
            acceptTermsButton.click();
        } catch (Exception e) {
            /*
             * Si le clic classique ne fonctionne pas,
             * on tente un clic JavaScript.
             */
            try {
                org.openqa.selenium.JavascriptExecutor javascript =
                        (org.openqa.selenium.JavascriptExecutor) driver;

                javascript.executeScript(
                        "arguments[0].click();",
                        acceptTermsButton
                );
            } catch (Exception ignored) {
            }
        }

        /*
         * On ne bloque pas le scraper si le bouton disparaît
         * déjà ou si Flashscore modifie son comportement.
         */
        try {
            new WebDriverWait(
                    driver,
                    Duration.ofSeconds(5)
            ).until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            FlashscoreSelectors.ACCEPT_TERMS_BUTTON_SELECTOR
                    )
            );
        } catch (Exception ignored) {
        }
    }
}
