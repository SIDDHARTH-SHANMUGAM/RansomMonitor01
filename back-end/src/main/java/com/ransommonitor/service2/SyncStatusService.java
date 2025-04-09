package com.ransommonitor.service2;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import com.ransommonitor.utils.OnlinePort;
import com.ransommonitor.utils.URLStatusChecker;

import java.sql.SQLException;
import java.util.List;

public class SyncStatusService {

    private AttackersSiteUrlsDaoImpl urlsDao = new AttackersSiteUrlsDaoImpl();

    public String syncStatus() throws SQLException, ClassNotFoundException {
        List<AttackerSiteUrl> urls = urlsDao.getAllUrls();

        if(OnlinePort.isPortOpen(9050)||OnlinePort.isPortOpen(9150)){
            for (AttackerSiteUrl url : urls) {
                boolean isActive = URLStatusChecker.checkOnionStatus(url.getURL(), 9050)||URLStatusChecker.checkOnionStatus(url.getURL(), 9150);
                url.setStatus(isActive);
                urlsDao.updateUrl(url);
            }
            return "Sync Success";
        }
        else {
            return "Tor is not Connected";
        }
    }

}
