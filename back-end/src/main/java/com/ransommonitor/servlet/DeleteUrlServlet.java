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



@WebServlet("/deleteUrl")
public class DeleteUrlServlet extends HttpServlet {
    private AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            // Convert JSON to Map
            Map<String, Object> requestMap = new Gson().fromJson(requestBody, Map.class);
            int urlId = ((Double) requestMap.get("urlId")).intValue();

            // delete URL to attacker
            attackersSiteUrlsDao.deleteUrl(urlId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": true}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to add URL\"}");
            e.printStackTrace();
        }
    }
}