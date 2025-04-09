package com.ransommonitor.servlet;

import com.google.gson.Gson;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/getAllAttackers")
public class GetAllAttackersServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetAllAttackersServlet.class.getName());
    private final AttackersDao attackersDao = new AttackersDaoImpl();
    private final AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Received GET request on /getAllAttackers");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Attacker> attackers = attackersDao.getAllAttackers();
            logger.info("Fetched " + attackers.size() + " attackers from database.");

            List<Map<String, Object>> result = new ArrayList<>();

            for (Attacker attacker : attackers) {
                logger.fine("Processing attacker: " + attacker.getAttackerName());

                Map<String, Object> attackerMap = new HashMap<>();
                attackerMap.put("attackerId", attacker.getAttackerId());
                attackerMap.put("attackerName", attacker.getAttackerName());
                attackerMap.put("monitorStatus", attacker.getMonitorStatus());

                List<AttackerSiteUrl> urls = attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName());
                logger.fine("Fetched " + urls.size() + " URLs for attacker: " + attacker.getAttackerName());


                attackerMap.put("urls", urls);
                result.add(attackerMap);
            }

            String json = new Gson().toJson(result);
            System.out.println(json);
            response.getWriter().write(json);
            logger.info("Successfully responded with attacker data.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException while fetching attackers: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch attackers\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Unexpected error occurred\"}");
        }
    }
}
