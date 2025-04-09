package com.ransommonitor.service2;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class UrlMonitoringService {
    private AttackersSiteUrlsDao urlsDao = new AttackersSiteUrlsDaoImpl();
    private AttackersDao attackersDao = new AttackersDaoImpl();
    private final Gson gson = new Gson();

    public void updateSingleUrlMonitoring(int urlId, boolean newStatus, HttpServletResponse response)
            throws SQLException, IOException {

        boolean success = urlsDao.updateUrlMonitoringStatus(urlId, newStatus);
        JsonObject responseJson = new JsonObject();

        if (success) {
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "URL monitoring status updated");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "URL not found or update failed");
        }

        response.getWriter().write(gson.toJson(responseJson));
    }

    public void updateAllUrlsForAttacker(int attackerId, boolean newStatus, HttpServletResponse response)
            throws SQLException, IOException {

        boolean success = attackersDao.updateAttackerMonitoringStatus(attackerId, newStatus);
        JsonObject responseJson = new JsonObject();

        if (success) {
            responseJson.addProperty("success", true);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "No URLs found for attacker or update failed");
        }

        response.getWriter().write(gson.toJson(responseJson));
    }

}
