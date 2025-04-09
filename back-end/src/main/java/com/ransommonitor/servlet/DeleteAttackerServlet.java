package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.service2.DeleteAttackerService;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/deleteAttacker")
public class DeleteAttackerServlet extends HttpServlet {

    private static final DeleteAttackerService deleteAttackerService = new DeleteAttackerService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            // Read and parse request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            Map<String, Object> requestMap = gson.fromJson(requestBody, Map.class);
            int attackerId = ((Double) requestMap.get("data")).intValue();

            String res = deleteAttackerService.deleteAttacker(attackerId);

            response.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for deleting attackers and their associated URLs";
    }
}