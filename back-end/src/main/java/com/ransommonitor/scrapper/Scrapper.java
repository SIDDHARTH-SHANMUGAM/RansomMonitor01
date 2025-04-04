package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;

import java.util.List;

public interface Scrapper {
    public List<Attack> scrapData(String url);
}
