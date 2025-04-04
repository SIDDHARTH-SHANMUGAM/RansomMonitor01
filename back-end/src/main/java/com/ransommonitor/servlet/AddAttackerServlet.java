package com.ransommonitor.servlet;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.service.AttackersService;
import com.ransommonitor.service.AttackersServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

    @WebServlet("/addAttacker")
    public class AddAttackerServlet extends HttpServlet {
        private AttackersService attackerService;

        @Override
        public void init() throws ServletException {
            super.init();
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            Attacker attacker = new Attacker();
            attacker.setAttackerName(request.getParameter("attackerName"));
            attacker.setEmail(request.getParameter("email"));
            attacker.setToxId(request.getParameter("toxId"));
            attacker.setSessionId(request.getParameter("sessionId"));
            attacker.setDescription(request.getParameter("description"));
            attacker.setFirstAttackAt(request.getParameter("firstAttackAt"));

            String isRAAS = request.getParameter("isRAAS");
            attacker.setRAAS(isRAAS != null && isRAAS.equalsIgnoreCase("true"));

            String monitorStatus = request.getParameter("monitorStatus");
            if (monitorStatus != null) {
                attacker.setMonitorStatus(Boolean.parseBoolean(monitorStatus));
            }

            boolean isSuccess = attackerService.addAttacker(attacker);

            if (isSuccess) {
                response.getWriter().write("Attacker added successfully");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.getWriter().write("Failed to add attacker. Please check your data.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

