package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/deleteAttacker")
public class DeleteAttackerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DeleteAttackerServlet.class.getName());
    private final AttackersDaoImpl attackersDao = new AttackersDaoImpl();
    private final AttackersSiteUrlsDaoImpl urlsDao = new AttackersSiteUrlsDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Received request to delete an attacker");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read and parse request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            logger.fine("Request body: " + requestBody);

            Map<String, Object> requestMap = gson.fromJson(requestBody, Map.class);
            int attackerId = ((Double) requestMap.get("data")).intValue();
            logger.info("Parsed attackerId: " + attackerId);

            // Delete all URLs for this attacker first
            List<AttackerSiteUrl> urls = urlsDao.getUrlsByAttackerId(attackerId);
            for (AttackerSiteUrl url : urls) {
                if (!urlsDao.deleteUrl(url.getUrlId())) {
                    logger.warning("Failed to delete URL with ID: " + url.getUrlId());
                    throw new Exception("Failed to delete one or more URLs for attacker");
                }
            }
            logger.info("Deleted " + urls.size() + " URLs for attacker");

            // Delete the attacker
            boolean attackerDeleted = attackersDao.deleteAttacker(attackerId);
            if (!attackerDeleted) {
                throw new Exception("Failed to delete attacker from database");
            }

            // Send success response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true, \"message\": \"Attacker and all URLs deleted successfully\"}");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting attacker: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for deleting attackers and their associated URLs";
    }
}