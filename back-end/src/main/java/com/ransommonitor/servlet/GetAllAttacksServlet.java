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
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetAllAttacksServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(GetAllAttacksServlet.class.getName());
    private final AttacksDao attacksDao = new AttacksDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("GetAllAttacksServlet doGet");
        logger.info("Received GET request on /getAllAttacks");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Attack> attacks = attacksDao.getAllAttacks();
            logger.info("Fetched " + attacks.size() + " attacks from database.");

            String json = new Gson().toJson(attacks);
            response.getWriter().write(json);
            logger.info("Successfully responded with attack data.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while fetching attacks", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to fetch attacks\"}");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error occurred while processing request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Unexpected error occurred\"}");
        }
    }
}
