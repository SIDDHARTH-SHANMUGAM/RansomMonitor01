package com.ransommonitor.servlet;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
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
    private static final int TOR_PROXY_PORT = 9050; // Default Tor proxy port

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AttackersSiteUrlsDaoImpl urlsDao = new AttackersSiteUrlsDaoImpl();

        try {

            List<AttackerSiteUrl> urls = urlsDao.getAllUrls();

            for (AttackerSiteUrl url : urls) {
                boolean isActive = URLStatusChecker.checkOnionStatus(url.getURL(), 9050)||URLStatusChecker.checkOnionStatus(url.getURL(), 9150);
                url.setStatus(isActive);
                urlsDao.updateUrl(url);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Status sync completed successfully");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error syncing status: " + e.getMessage());
            e.printStackTrace();
        }
    }
}