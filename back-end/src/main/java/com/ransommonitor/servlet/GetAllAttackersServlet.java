package com.ransommonitor.servlet;

import com.google.gson.Gson;
import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/getAllAttackers")
public class GetAllAttackersServlet extends HttpServlet {

private AttackersDao attackersDao = new AttackersDaoImpl();
private AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Attacker> attackers = attackersDao.getAllAttackers();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Attacker attacker : attackers) {
                Map<String, Object> attackerMap = new HashMap<>();
                attackerMap.put("attackerId", attacker.getAttackerId());
                attackerMap.put("attackerName", attacker.getAttackerName());
                attackerMap.put("monitorStatus", attacker.getMonitorStatus());
                attackerMap.put("urls", attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName()));
                result.add(attackerMap);
            }

            String json = new Gson().toJson(result);
            response.getWriter().write(json);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch attackers\"}");
            e.printStackTrace();
        }
    }
}