package com.github.brunomndantas.flashscore.api.transversal.driverPool;

import com.github.brunomndantas.jscrapper.core.driverSupplier.DriverSupplierException;
import com.github.brunomndantas.jscrapper.core.driverSupplier.IDriverSupplier;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.LinkedList;

public class DriverPool implements IDriverPool {

    private final Object lock = new Object();

    private final Collection<WebDriver> drivers = new LinkedList<>();
    private final LinkedList<WebDriver> freeDrivers = new LinkedList<>();

    private boolean closed = false;

    private final IDriverSupplier driverSupplier;
    private final int maxInstances;

    public DriverPool(IDriverSupplier driverSupplier, int maxInstances) {
        this.driverSupplier = driverSupplier;
        this.maxInstances = maxInstances;
    }

    @Override
    public WebDriver getDriver() throws DriverPoolException {

        synchronized (lock) {

            while (true) {

                if (closed) {
                    throw new DriverPoolException("Driver is closed!");
                }

                /*
                 * On récupère un driver libre.
                 */
                if (!freeDrivers.isEmpty()) {

                    WebDriver driver = getDriverFromPool();

                    /*
                     * Vérifie que Chrome fonctionne encore.
                     */
                    if (isDriverAlive(driver)) {
                        return driver;
                    }

                    /*
                     * Chrome est mort :
                     * on le supprime et on en crée un nouveau.
                     */
                    removeDriver(driver);

                } else if (drivers.size() < maxInstances) {

                    return getNewDriver();

                } else {

                    await();
                }
            }
        }
    }

    private WebDriver getNewDriver() throws DriverPoolException {

        try {

            WebDriver driver = driverSupplier.get();

            drivers.add(driver);

            return driver;

        } catch (DriverSupplierException e) {

            throw new DriverPoolException(
                    "Error getting driver!",
                    e
            );
        }
    }

    private WebDriver getDriverFromPool() {

        return freeDrivers.removeFirst();
    }

    private void await() throws DriverPoolException {

        try {

            lock.wait();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new DriverPoolException(
                    "Wait was interrupted!",
                    e
            );
        }
    }

    @Override
    public void returnDriver(WebDriver driver) throws DriverPoolException {

        synchronized (lock) {

            if (driver == null) {
                return;
            }

            if (!drivers.contains(driver)) {
                throw new DriverPoolException(
                        "This driver doesn't belong to this pool!"
                );
            }

            if (freeDrivers.contains(driver)) {
                throw new DriverPoolException(
                        "This driver was already released!"
                );
            }

            /*
             * Vérifie si Chrome est encore vivant.
             */
            if (!isDriverAlive(driver)) {

                removeDriver(driver);

                /*
                 * Réveille les requêtes qui attendent
                 * afin qu'un nouveau Chrome puisse être créé.
                 */
                lock.notifyAll();

                return;
            }

            if (closed) {

                try {
                    driver.quit();
                } catch (Exception ignored) {
                }

                drivers.remove(driver);

                return;
            }

            freeDrivers.add(driver);

            lock.notifyAll();
        }
    }

    /*
     * Vérifie si le navigateur est toujours utilisable.
     */
    private boolean isDriverAlive(WebDriver driver) {

        if (driver == null) {
            return false;
        }

        try {

            driver.getWindowHandle();

            return true;

        } catch (Exception e) {

            return false;
        }
    }

    /*
     * Supprime complètement un driver cassé.
     */
    private void removeDriver(WebDriver driver) {

        freeDrivers.remove(driver);
        drivers.remove(driver);

        try {
            driver.quit();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() {

        synchronized (lock) {

            for (WebDriver driver : drivers) {

                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }

            drivers.clear();
            freeDrivers.clear();

            closed = true;

            lock.notifyAll();
        }
    }
}
