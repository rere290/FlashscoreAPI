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

            driver = sourceSupplier.get();

            /*
             * Pas de maximize() sur Railway/Linux headless.
             */

            driver.get(FlashscoreURLs.FLASHSCORE_URL);

            /*
             * Le bandeau de consentement peut être absent.
             * Dans ce cas on continue normalement.
             */
            acceptTerms(driver);

            return driver;

        } catch (Exception e) {

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

        try {

            WebDriverWait wait = new WebDriverWait(
                    driver,
                    Duration.ofSeconds(10)
            );

            WebElement acceptTermsButton =
                    wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    FlashscoreSelectors.ACCEPT_TERMS_BUTTON_SELECTOR
                            )
                    );

            acceptTermsButton.click();

            /*
             * Petite attente pour laisser disparaître
             * le bandeau.
             */
            try {

                wait.until(
                        ExpectedConditions.invisibilityOfElementLocated(
                                FlashscoreSelectors.ACCEPT_TERMS_BUTTON_SELECTOR
                        )
                );

            } catch (Exception ignored) {
                /*
                 * Le bouton peut avoir disparu immédiatement
                 * après le clic.
                 */
            }

        } catch (Exception ignored) {

            /*
             * Pas de bouton de consentement :
             * on continue quand même.
             *
             * Flashscore peut afficher le site directement
             * selon la session / région / cookies.
             */
        }
    }
}
