package com.ransommonitor.servlet;

import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private static final Logger logger = Logger.getLogger(UrlMonitoringServlet.class.getName());

    private AttackersSiteUrlsDao urlsDao;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        urlsDao = new AttackersSiteUrlsDaoImpl();
        logger.info("UrlMonitoringServlet initialized.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Received POST request to /updateMonitoring");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            BufferedReader reader = request.getReader();
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            boolean newStatus = jsonObject.get("newStatus").getAsBoolean();
            Integer urlId = jsonObject.has("urlId") ? jsonObject.get("urlId").getAsInt() : null;
            Integer attackerId = jsonObject.has("attackerId") ? jsonObject.get("attackerId").getAsInt() : null;

            logger.info("Parsed newStatus: " + newStatus + ", urlId: " + urlId + ", attackerId: " + attackerId);

            if (urlId != null) {
                logger.info("Updating monitoring status for a single URL");
                updateSingleUrlMonitoring(urlId, newStatus, response);
            } else if (attackerId != null) {
                logger.info("Updating monitoring status for all URLs of attacker");
                updateAllUrlsForAttacker(attackerId, newStatus, response);
            } else {
                logger.warning("Invalid request: missing attackerId or urlId");
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Either attackerId or urlId must be provided");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while processing monitoring update", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error processing request: " + e.getMessage());
        }
    }

    private void updateSingleUrlMonitoring(int urlId, boolean newStatus, HttpServletResponse response)
            throws SQLException, IOException {
        logger.info("Updating single URL (urlId=" + urlId + ") to newStatus: " + newStatus);

        boolean success = urlsDao.updateUrlMonitoringStatus(urlId, newStatus);
        JsonObject responseJson = new JsonObject();

        if (success) {
            logger.info("Successfully updated URL monitoring status.");
            responseJson.addProperty("success", true);
            responseJson.addProperty("message", "URL monitoring status updated");
        } else {
            logger.warning("Failed to update URL monitoring status. URL not found.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "URL not found or update failed");
        }

        response.getWriter().write(gson.toJson(responseJson));
    }

    private void updateAllUrlsForAttacker(int attackerId, boolean newStatus, HttpServletResponse response)
            throws SQLException, IOException {
        logger.info("Updating all URLs for attackerId=" + attackerId + " to newStatus: " + newStatus);

        boolean success = urlsDao.updateAttackerMonitoringStatus(attackerId, newStatus);
        JsonObject responseJson = new JsonObject();

        if (success) {
            logger.info("Successfully updated monitoring status for all URLs of attacker.");
            responseJson.addProperty("success", true);
        } else {
            logger.warning("No URLs found for attacker or update failed.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseJson.addProperty("success", false);
            responseJson.addProperty("message", "No URLs found for attacker or update failed");
        }

        response.getWriter().write(gson.toJson(responseJson));
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        logger.warning("Sending error response: " + message);
        response.setStatus(statusCode);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("success", false);
        errorJson.addProperty("message", message);
        response.getWriter().write(gson.toJson(errorJson));
    }
}
