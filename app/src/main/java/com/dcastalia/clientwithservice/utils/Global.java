package com.dcastalia.clientwithservice.utils;

import java.net.Socket;

/**
 * Created by piashsarker on 8/20/17.
 */

public class Global  {

    private static Socket serverSocket ;

    public static Socket getServerSocket() {
        return serverSocket;
    }

    public static void setServerSocket(Socket serverSocket) {
        Global.serverSocket = serverSocket;
    }
}
