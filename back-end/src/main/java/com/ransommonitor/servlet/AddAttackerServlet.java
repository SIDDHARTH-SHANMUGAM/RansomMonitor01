package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.ransommonitor.service.AttackerSiteUrlSevice;
import com.ransommonitor.service.AttackersService;
import com.ransommonitor.service.AttackersServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/addAttacker")
public class AddAttackerServlet extends HttpServlet {
    private AttackersDao attackersDao = new AttackersDaoImpl();
    private AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    private Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read JSON request body
        BufferedReader reader = request.getReader();
        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
            System.out.println(line);
        }

        try {
            // Convert JSON to Java object using Gson
            AttackerRequest attackerRequest = gson.fromJson(jsonBody.toString(), AttackerRequest.class);

            // Create Attacker object
            Attacker attacker = new Attacker();
            attacker.setAttackerName(attackerRequest.getAttackerName());

            System.out.println(attacker);

            // Save to database
            String s=  attackersDao.addNewAttacker(attacker);
            System.out.println(s);

            for(String url : attackerRequest.getUrls())
            {
                attackersSiteUrlsDao.addNewUrl(new AttackerSiteUrl(attacker.getAttackerId(), url, true, true, new Date().toString()));
            }

            if (s.equals("Added Attacker")) {
                response.getWriter().write("Attacker added successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.getWriter().write("Failed to add attacker.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (JsonSyntaxException e) {
            response.getWriter().write("Invalid JSON format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
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
