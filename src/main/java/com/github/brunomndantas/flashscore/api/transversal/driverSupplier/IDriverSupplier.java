package com.github.brunomndantas.flashscore.api.transversal.driverSupplier;

import org.openqa.selenium.WebDriver;

public interface IDriverSupplier {

    WebDriver get() throws DriverSupplierException;

}
