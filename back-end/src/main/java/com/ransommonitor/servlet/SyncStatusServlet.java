package com.ransommonitor.servlet;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.ransommonitor.service2.SyncStatusService;
import com.ransommonitor.utils.URLStatusChecker;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/syncStatus")
public class SyncStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            SyncStatusService syncStatusService = new SyncStatusService();
            syncStatusService.syncStatus();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Status sync completed successfully");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error syncing status: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}