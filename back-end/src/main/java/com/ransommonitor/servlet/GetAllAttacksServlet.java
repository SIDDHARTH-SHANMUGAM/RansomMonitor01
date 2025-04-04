package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.Attack;
import com.ransommonitor.dao.AttacksDao;
import com.ransommonitor.dao.AttacksDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/getAllAttacks")
public class GetAllAttacksServlet extends HttpServlet {

    private AttacksDao attacksDao = new AttacksDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Attack> attacks = attacksDao.getAllAttacks();
            String json = new Gson().toJson(attacks);
            response.getWriter().write(json);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch attacks\"}");
            e.printStackTrace();
        }
    }
}
