package com.github.brunomndantas.flashscore.api.serviceInterface.config;

import com.github.brunomndantas.flashscore.api.logic.services.scrapService.IScrapService;
import com.github.brunomndantas.flashscore.api.logic.services.scrapService.Report;
import com.github.brunomndantas.flashscore.api.logic.services.entityScrapper.EntityScrapperException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ScrapStartup implements ApplicationRunner {

    private final IScrapService scrapService;

    public ScrapStartup(IScrapService scrapService) {
        this.scrapService = scrapService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            System.out.println("========================================");
            System.out.println("Starting Flashscore scraper...");
            System.out.println("========================================");

            Report report = new Report();
            scrapService.scrap(report);

            System.out.println("========================================");
            System.out.println("Flashscore scraper finished.");
            System.out.println("========================================");

        } catch (EntityScrapperException e) {
            System.err.println("Flashscore scraper failed:");
            e.printStackTrace();
        }
    }
}
