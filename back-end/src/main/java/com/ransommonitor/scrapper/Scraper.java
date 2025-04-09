package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;

import java.util.List;

public interface Scraper {
    public List<Attack> scrapeData(String url);
}
