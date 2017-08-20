package com.dcastalia.clientwithservice.thread;

import com.dcastalia.clientwithservice.utils.Constant;
import com.dcastalia.clientwithservice.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by piashsarker on 8/20/17.
 */

public class ServerConnectionThread extends Thread {

    private static String ipAddress;
    private  Socket socket ;
    private OutputStream outputStream = null ;
    private InputStream inputStream = null ;
    private PrintWriter printWriter = null ;

    public ServerConnectionThread(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ipAddress, Constant.SERVER_PORT);
            if(socket.isConnected()){
                Utils.log("Socket Connected ! ");
                outputStream =socket.getOutputStream();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
