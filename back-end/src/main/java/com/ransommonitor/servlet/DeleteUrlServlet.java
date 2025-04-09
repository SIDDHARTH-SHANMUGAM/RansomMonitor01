package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.ransommonitor.service2.DeleteUrlService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/deleteUrl")
public class DeleteUrlServlet extends HttpServlet {

    private final DeleteUrlService deleteUrlService=new DeleteUrlService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            Map<String, Object> requestMap = new Gson().fromJson(requestBody, Map.class);

            if (!requestMap.containsKey("data")) {
                throw new IllegalArgumentException("Missing urlId in request");
            }

            int urlId = ((Double) requestMap.get("data")).intValue();

            // Delete URL from database
            String res = deleteUrlService.deleteUrl(urlId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
