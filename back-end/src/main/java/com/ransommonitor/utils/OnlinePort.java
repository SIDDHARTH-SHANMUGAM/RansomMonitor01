package com.ransommonitor.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class OnlinePort {
    public static boolean isPortOpen( int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port), 2000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

//    public static int  getOnline()
//    {
//        int port[] = {9050, 9150};
//        isPortOpen()
//    }
}
