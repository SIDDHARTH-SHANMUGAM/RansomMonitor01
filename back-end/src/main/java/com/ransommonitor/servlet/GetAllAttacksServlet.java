package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.Attack;
import com.ransommonitor.service2.GetAllAttacksService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class GetAllAttacksServlet extends HttpServlet {

    private final GetAllAttacksService getAllAttacksService = new GetAllAttacksService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("GetAllAttacksServlet doGet");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Attack> attacks = getAllAttacksService.getAllAtacks();

            String json = new Gson().toJson(attacks);
            response.getWriter().write(json);
        } catch (SQLException | IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
