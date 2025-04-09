package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.service2.AddAttackerService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/addAttacker")
public class AddAttackerServlet extends HttpServlet {

    private final AddAttackerService addAttackerService = new AddAttackerService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    try {
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();

        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }

            AttackerRequest attackerRequest = gson.fromJson(jsonBody.toString(), AttackerRequest.class);
            Attacker attacker = new Attacker();
            attacker.setAttackerName(attackerRequest.getAttackerName());
            String result = addAttackerService.addNewAttacker(attacker, attackerRequest.getUrls());

            if ("Added Attacker".equals(result)) {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        } catch (JsonSyntaxException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } catch (IOException e) {

        } catch (Exception e) {
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
