package com.dcastalia.clientwithservice.thread;

import com.dcastalia.clientwithservice.utils.Constant;
import com.dcastalia.clientwithservice.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by piashsarker on 8/20/17.
 */

public class ServerConnectionThread extends Thread {

    private static String ipAddress;
    private static Socket socket ;
    private PrintWriter printWriter ;
    private boolean messageReceiving=false ;
    private BufferedReader bufferedIn ;
    private OnMessageReceived mMessageListener ;
    private String mServerMessage ;


    public ServerConnectionThread(String ipAddress , OnMessageReceived mMessageListener) {
        this.mMessageListener = mMessageListener ;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        try {

            socket = new Socket(ipAddress, Constant.SERVER_PORT);
            if(socket.isConnected()){
                Utils.log("Socket Connected ! ");
                messageReceiving = true ;

                /** Initialize the printWriter for writing in the socket or sent data**/
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back

                /** Sent  a welcome message to server **/
                sendData(" Hello I am Client "+socket.getInetAddress().getHostAddress());

                while (messageReceiving){

                    if(!socket.isClosed()){
                        bufferedIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        // Check the the incoming stream leng by read method if return -1 than the client is not online **/

                        if(bufferedIn.read()!=-1){
                            mServerMessage = bufferedIn.readLine();
                            if(mServerMessage!=null && mMessageListener!=null){
                                Utils.log("Message From Server : "+mServerMessage);

                                /** Pass the message to this interface so that each class can listen or read who implemented the interface **/
                                mMessageListener.messageReceived(mServerMessage);
                            }
                        }


                        else{
                            // Close the server socket and stop receving
                            Utils.log("Server is not online, So Closing the connection");
                            messageReceiving= false ;
                            socket.close();
                            this.interrupt();
                        }



                    }


                }

            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        sendData("Connection Closing ...");
        messageReceiving = false;

        if(socket!=null){
            try {
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if ( printWriter!= null) {
            printWriter.flush();
            printWriter.close();
        }

        mMessageListener = null;
        bufferedIn = null;
        printWriter = null;
        mServerMessage = null;
    }




    public boolean sendData(String data){
        boolean isSent  = false;
            if(socket!=null && socket.isConnected()){
                if(printWriter!=null && !printWriter.checkError()){
                    printWriter.println(data);
                    Utils.log("Data Sending"+ data);
                    isSent = true;
                }

            }
            else{
                Utils.log("Socket Null or Disconnected! ");
            }


        return  isSent ;
    }
    /** Delclare the  interface .
     * This method listen for every time when message recived from the socket. **/
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
