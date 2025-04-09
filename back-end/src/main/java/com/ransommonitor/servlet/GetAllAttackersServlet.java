package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.service2.GetAllAttackersService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/getAllAttackers")
public class GetAllAttackersServlet extends HttpServlet {

    private static final GetAllAttackersService getAllAttackersService = new GetAllAttackersService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            List<Map<String, Object>> result = getAllAttackersService.getAllAttackers();
            String json = new Gson().toJson(result);
            response.getWriter().write(json);
        } catch (SQLException | IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
