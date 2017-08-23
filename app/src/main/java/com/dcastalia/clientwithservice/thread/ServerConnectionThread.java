package com.dcastalia.clientwithservice.thread;

import android.graphics.Bitmap;

import com.dcastalia.clientwithservice.utils.Constant;
import com.dcastalia.clientwithservice.utils.MyApplication;
import com.dcastalia.clientwithservice.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by piashsarker on 8/20/17.
 */

public class ServerConnectionThread extends Thread {

    private static String ipAddress;
    private static Socket socket;
    private boolean messageReceiving = false;

    private OnMessageReceived mMessageListener;
    private String mServerMessage;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private String clientIp = null;
    private String command;
    private boolean isReachable = false;

    public ServerConnectionThread(String ipAddress, OnMessageReceived mMessageListener) {
        this.mMessageListener = mMessageListener;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        try {

            socket = new Socket(ipAddress, Constant.SERVER_PORT);
            if (socket.isConnected()) {
                Utils.log("Socket Connected ! ");
                clientIp = socket.getInetAddress().getHostAddress();

                // attach data inputStream for listening incoming stream from the socket

                dataInputStream = new DataInputStream(socket.getInputStream());

                //attach data outPutStream for sending output stream through this socket
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                messageReceiving = true;

                /** Sent  a welcome message to server **/
                sendData(" Hello I am Client " + Utils.getWifiIpAddress(MyApplication.getInstance()));

                while (messageReceiving) {
                    Utils.log("Message Receiving Thread Running");
                    // check the inputStream read for incoming data , return -1 if client disconnected in
                    // other end.


                    if (dataInputStream.read() != -1) {
                        command = dataInputStream.readUTF();
                        Utils.log("Message Received : " + command);

                        /** Read the command and manages the sequece of reading data from the input stream , if sequence breaks
                         * than the program will be stuck. Must manage the sequences . We are working with two type of data String , Byte Array .
                         * #Check which types of data are the client sending
                         * if byte[] than it's bitmap of screen sharing otherwise all command and data are
                         * string
                         */

                        if (command.equals(Constant.STRING_TYPE_DATA)) {
                            String message = dataInputStream.readUTF();
                            Utils.log("Message From Client " + message);
                            if (message != null && mMessageListener != null) {
                                /** Pass the message to service and let service process action based on it**/
                                mMessageListener.messageReceived(message);
                            }
                        }
                        if (command.equals(Constant.BYTE_ARRAY_DATA)) {
                            /** This byte array consist the data of bitmap images **? First read is for length and second is for byte array  */
                            int length = dataInputStream.readInt();
                            /** Read the full byte from the inputStream using readFully()**/

                            if (length > 0) {
                                byte[] bitmapArray = new byte[length];
                                dataInputStream.readFully(bitmapArray, 0, bitmapArray.length);
                                /** Send the @bitmapArray to service and let service process it for each request **/
                                if (bitmapArray != null && mMessageListener != null) {
                                    mMessageListener.bitmapReceived(bitmapArray);
                                    Utils.log(" Sending Bitmap To Service ! ");
                                }
                                Utils.log(" Bitmap Array Received from " + clientIp);
                            }


                        }


                    } else {
                        mMessageListener.messageReceived("Client Disconnected " + clientIp);
                        Utils.log("Client Disconnected " + ipAddress);
                        messageReceiving = false;
                    }

                }

            } else {
                Utils.log("Not Connected ! Retry ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {

        sendData("Connection Closing ...");
        messageReceiving = false;

        mMessageListener = null;
        mServerMessage = null;
        try {
            dataInputStream.close();
            dataOutputStream.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    public boolean sendData(String data) {
        boolean isSent = false;
        /** Must manage sequence when writing data using dataOutPutStream , in client end you should use this sequence to receive data**/

        try {
            /** first int send for checking , second one is command of dataType , third is data which should be send
             *  flush the data so that it can reach the client point , will close the dataOutPutStream when close the client  **/
            dataOutputStream.write(1);
            dataOutputStream.writeUTF(Constant.STRING_TYPE_DATA);
            dataOutputStream.writeUTF(data);
            dataOutputStream.flush();
            Utils.log(data + " Send Successfull to " + ipAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSent;
    }

    public void sendBitmap(Bitmap bitmap) {


        try {
            /** Convert the bitmap into a byteArray so that we can send it using outPutStream **/
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] outBuffer = stream.toByteArray();
            int length = outBuffer.length;

            /** Must manage the sequence , first one is a int for checking ,
             * second one is the command for byteArrayType , thirdOne is length and fourth is byteArray **/

            dataOutputStream.write(1);
            dataOutputStream.writeUTF(Constant.BYTE_ARRAY_DATA);
            dataOutputStream.writeInt(length);
            dataOutputStream.write(outBuffer);
            dataOutputStream.flush();
            Utils.log("Bitmap byteArray send successfully ! to " + ipAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * Delclare the  interface .
     * This method listen for every time when message recived from the socket.
     **/
    public interface OnMessageReceived {
        void messageReceived(String message);

        void bitmapReceived(byte[] bitmapArray);
    }

}
