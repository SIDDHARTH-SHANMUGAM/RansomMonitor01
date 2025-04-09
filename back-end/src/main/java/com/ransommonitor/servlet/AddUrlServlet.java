package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.service2.AddUrlService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/addUrl")
public class AddUrlServlet extends HttpServlet {

    private final AddUrlService addUrlService= new AddUrlService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            Map<String, Object> requestMap = new Gson().fromJson(requestBody, Map.class);
            int attackerId = ((Double) requestMap.get("attackerId")).intValue();
            String url = (String) requestMap.get("url");

            String s = addUrlService.addNewUrl(new AttackerSiteUrl(attackerId, url));
            response.setStatus(HttpServletResponse.SC_OK);

        }catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
