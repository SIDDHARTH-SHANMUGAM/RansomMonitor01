package com.ransommonitor.scrapper;

public class ScraperFactory {
    public static Scraper getScrapper(String type) {
        if (type.equalsIgnoreCase("babuk")) {
            return new BabukScraper();
        } else if (type.equalsIgnoreCase("bashe")) {
            return new BasheScraper();
        } else if (type.equalsIgnoreCase("vanhelsing")) {
            return new VanHelsingScraper();
        } else if (type.equalsIgnoreCase("killsec")) {
            return new KillsecScraper();
        } else if (type.equalsIgnoreCase("runsomeware")) {
            return new RunSomeWareScraper();
        } else if (type.equalsIgnoreCase("monti")) {
            return new MontiScraper();
        } else if (type.equalsIgnoreCase("oxthief")) {
            return null;
        } else if (type.equalsIgnoreCase("fog")) {
            return new FogScraper();
        } else if (type.equalsIgnoreCase("weyhro")) {
            return new WeyhroScraper();
        } else if (type.equalsIgnoreCase("cracyhunter")) {
            return null;
        } else if (type.equalsIgnoreCase("akira")) {
            return new AkiraScraper();
        } else if (type.equalsIgnoreCase("clop")) {
            return null;
        } else if (type.equalsIgnoreCase("medusa")) {
            return null;
        }else if (type.equalsIgnoreCase("nightspire")) {
            return new NightSpireScraper();
        }
        throw new IllegalArgumentException("Unknown attacker type: " + type);
    }
}
