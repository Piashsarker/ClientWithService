package com.dcastalia.clientwithservice.utils;

import android.app.Application;

import com.dcastalia.clientwithservice.boardCastReceiver.ConnectivityReceiver;


/**
 * Created by PT on 8/20/2017.
 */

public class MyApplication extends Application{


    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
