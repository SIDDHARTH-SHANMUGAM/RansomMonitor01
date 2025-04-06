package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/addUrl")
public class AddUrlServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddUrlServlet.class.getName());
    private final AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Received request to add a new attacker URL.");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            logger.fine("Request body: " + requestBody);

            Map<String, Object> requestMap = new Gson().fromJson(requestBody, Map.class);
            int attackerId = ((Double) requestMap.get("attackerId")).intValue();
            String url = (String) requestMap.get("url");

            logger.info("Parsed attackerId: " + attackerId + ", URL: " + url);

            attackersSiteUrlsDao.addNewUrl(new AttackerSiteUrl(attackerId, url, true, true, ""));
            logger.info("URL added to attacker successfully.");

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to add URL: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to add URL\"}");
        }
    }
}
