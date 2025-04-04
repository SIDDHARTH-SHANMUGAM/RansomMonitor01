package com.ransommonitor.scrapper;

public class ScrapperFactory {
    public static Scrapper getScrapper(String type) {
        if (type.equalsIgnoreCase("babuk")) {
            return new BabukScrapper();
        } else if (type.equalsIgnoreCase("bashe")) {
            return new BasheScrapper();
        } else if (type.equalsIgnoreCase("vanhelsing")) {
            return new VanHelsingScrapper();
        } else if (type.equalsIgnoreCase("killsec")) {
            return new KillsecScrapper();
        }
        throw new IllegalArgumentException("Unknown attacker type: " + type);
    }
}
