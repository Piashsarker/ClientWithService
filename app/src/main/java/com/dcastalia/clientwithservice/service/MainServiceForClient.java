package com.dcastalia.clientwithservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dcastalia.clientwithservice.thread.ServerConnectionThread;
import com.dcastalia.clientwithservice.utils.Constant;

/**
 * Created by piashsarker on 8/20/17.
 */

public class MainServiceForClient extends Service {

    private static  final String TAG = "MainServiceForServer";
    private static boolean serviceRunning = false ;
    private String IPAddress = "";
    private IBinder binder = new LocalServerBinder();
    private ServerConnectionThread serverConnectionThread ;
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

        serverConnectionThread = new ServerConnectionThread(IPAddress);
        serverConnectionThread.start();
        return this.binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() Called");
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

        super.onDestroy();
    }








}
