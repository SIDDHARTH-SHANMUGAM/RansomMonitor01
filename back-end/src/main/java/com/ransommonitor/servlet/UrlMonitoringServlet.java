package com.ransommonitor.servlet;

import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ransommonitor.service2.UrlMonitoringService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/updateMonitoring")
public class UrlMonitoringServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        UrlMonitoringService urlMonitoringService = new UrlMonitoringService();

        try {
            BufferedReader reader = request.getReader();
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            boolean newStatus = jsonObject.get("newStatus").getAsBoolean();
            Integer urlId = jsonObject.has("urlId") ? jsonObject.get("urlId").getAsInt() : null;
            Integer attackerId = jsonObject.has("attackerId") ? jsonObject.get("attackerId").getAsInt() : null;


            if (urlId != null) {
                urlMonitoringService.updateSingleUrlMonitoring(urlId, newStatus, response);
            } else if (attackerId != null) {
                urlMonitoringService.updateAllUrlsForAttacker(attackerId, newStatus, response);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
        }
    }
}
