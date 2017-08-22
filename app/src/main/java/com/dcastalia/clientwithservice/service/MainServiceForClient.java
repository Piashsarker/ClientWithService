package com.dcastalia.clientwithservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dcastalia.clientwithservice.thread.ServerConnectionThread;
import com.dcastalia.clientwithservice.utils.Constant;
import com.dcastalia.clientwithservice.utils.Utils;

/**
 * Created by piashsarker on 8/20/17.
 */

public class MainServiceForClient extends Service {

    private static  final String TAG = "MainServiceForServer";
    private static boolean serviceRunning = false ;
    private String IPAddress = "";
    private IBinder binder = new LocalServerBinder();
    private ServerConnectionThread serverConnectionThread;
    private String message  ;







    public class LocalServerBinder extends Binder {
        public MainServiceForClient getService(){
            return    MainServiceForClient.this;
        }
    }

    public MainServiceForClient() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBinder() Called");

        serviceRunning = true ;
        // Start Server Thread For Listening Client Connection .
        if(intent!=null){
            IPAddress  = intent.getStringExtra(Constant.IP_ADDRESS_KEY);
        }

        serverConnectionThread = new ServerConnectionThread(IPAddress, new ServerConnectionThread.OnMessageReceived() {
            @Override
            public void messageReceived(String message) {
                /** For Handling Messages Upcoming from the server and passing it using boardcast receiver **/
                Intent intent = new Intent(Constant.ACTION_MESSAGE);
                intent.putExtra(Constant.WELCOME_MESSAGE_KEY, message);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(intent);
            }

            @Override
            public void bitmapReceived(byte[] bitmapArray) {

            }
        });
        serverConnectionThread.start();
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() Called");
        /** Stop Server Connection Thread and Close All Communication **/

            serverConnectionThread.sendData(" Client Disconnected "+ Utils.getWifiIpAddress(getApplicationContext()));

            if(serverConnectionThread!=null){
                serverConnectionThread.close();
            }



        return  true ;

    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind() Called");
        super.onRebind(intent);

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate() Called");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() Called");
        return START_NOT_STICKY ;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy  Called");
        serverConnectionThread.sendData(" Client Disconnected "+ Utils.getWifiIpAddress(getApplicationContext()));
        /** Stop Server Connection Thread and Close All Communication **/
        if(serverConnectionThread!=null){
            serverConnectionThread.close();
        }
        super.onDestroy();
    }


    public void sendMessage(String message){
        serverConnectionThread.sendData(message);
    }





}
