package com.ransommonitor.servlet;

import com.google.gson.Gson;
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

@WebServlet("/deleteUrl")
public class DeleteUrlServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DeleteUrlServlet.class.getName());
    private final AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Received request to delete attacker site URL");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            logger.fine("Request body: " + requestBody);

            // Convert JSON to Map
            Map<String, Object> requestMap = new Gson().fromJson(requestBody, Map.class);
            logger.fine("Parsed request JSON: " + requestMap);

            // Validate keys
            if (!requestMap.containsKey("data")) {
                logger.warning("Missing 'data' key in request");
                throw new IllegalArgumentException("Missing urlId in request");
            }

            // Convert `urlId` to integer
            int urlId = ((Double) requestMap.get("data")).intValue();
            logger.info("Parsed urlId to delete: " + urlId);

            // Delete URL from database
            attackersSiteUrlsDao.deleteUrl(urlId);
            logger.info("Successfully deleted URL with id: " + urlId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while deleting URL", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to delete URL\"}");
        }
    }
}
