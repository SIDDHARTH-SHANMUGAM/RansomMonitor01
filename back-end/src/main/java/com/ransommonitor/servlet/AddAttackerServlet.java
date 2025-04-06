package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.ransommonitor.service.AttackersService;
import com.ransommonitor.service.AttackersServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/addAttacker")
public class AddAttackerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AddAttackerServlet.class.getName());

    private final AttackersService attackersService = new AttackersServiceImpl();
    private final AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Received request to add a new attacker.");

        BufferedReader reader = request.getReader();
        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

        logger.fine("Request JSON body: " + jsonBody);

        try {
            AttackerRequest attackerRequest = gson.fromJson(jsonBody.toString(), AttackerRequest.class);

            Attacker attacker = new Attacker();
            attacker.setAttackerName(attackerRequest.getAttackerName());

            logger.info("Parsed attacker: " + attacker.getAttackerName());

            String result = attackersService.addNewAttacker(attacker, attackerRequest.getUrls());
            logger.info("DAO result: " + result);


            if ("Added Attacker".equals(result)) {
                response.getWriter().write("Attacker added successfully");
                response.setStatus(HttpServletResponse.SC_OK);
                logger.info("Attacker added successfully.");
            } else {
                response.getWriter().write("Failed to add attacker.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                logger.warning("Failed to add attacker.");
            }

        } catch (JsonSyntaxException e) {
            logger.log(Level.SEVERE, "Invalid JSON format: " + e.getMessage(), e);
            response.getWriter().write("Invalid JSON format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing request: " + e.getMessage(), e);
            response.getWriter().write("Error processing request: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Helper class to map incoming JSON
    private static class AttackerRequest {
        private String attackerName;
        private List<String> urls;

        public String getAttackerName() {
            return attackerName;
        }

        public List<String> getUrls() {
            return urls;
        }
    }
}
