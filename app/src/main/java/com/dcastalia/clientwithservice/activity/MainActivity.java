package com.dcastalia.clientwithservice.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dcastalia.clientwithservice.R;
import com.dcastalia.clientwithservice.boardCastReceiver.ConnectivityReceiver;
import com.dcastalia.clientwithservice.service.MainServiceForClient;
import com.dcastalia.clientwithservice.utils.Constant;
import com.dcastalia.clientwithservice.utils.MyApplication;
import com.dcastalia.clientwithservice.utils.Utils;

public class MainActivity extends AppCompatActivity  implements ConnectivityReceiver.ConnectivityReceiverListener{
    private boolean binded = false;
    private MainServiceForClient serverService;
    private Button buttonConnect, buttonDisconnect;
    private EditText editTextIpAddress;
    private final String TAG = "MainActivity";
    private TextView textConnectStatus ;
    private String ipAddress ;


    ServiceConnection weatherServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainServiceForClient.LocalServerBinder binder = (MainServiceForClient.LocalServerBinder) service;
            serverService = binder.getService();
            binded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIp();
            }
        });


        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               stopServerService();
            }
        });


    }

    private void checkIp() {

        ipAddress = editTextIpAddress.getText().toString();

        if(Utils.validateIP(ipAddress)){
            checkConnection();
        }
        else{
            Utils.longToast(MainActivity.this, "Enter Correct IP");
        }
    }

    private void findViews() {

        editTextIpAddress = (EditText) findViewById(R.id.editText1);
        buttonConnect = (Button) findViewById(R.id.button1);
        textConnectStatus = (TextView) findViewById(R.id.txt_connect_status);
        buttonDisconnect = (Button) findViewById(R.id.btn_disconnect);
        buttonDisconnect.setEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }





    /** Do Start Server Work Here. This is a actionPerforming Method for button start Server **/


    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        manageServerService(isConnected);
    }

    private void manageServerService(boolean isConnected) {
        if(isConnected){

            if(ipAddress==null){
                Utils.longToast(MainActivity.this , "Enter Validate Ip");
            }
            else{
                startServerService();
                Utils.longToast(this,  "Connection Available For Starting Service");
            }

        }
        else{
            Utils.longToast(this, "Connection Down , Closing All Service and Threads");
            stopServerService();

        }

    }



    private void stopServerService() {

        if (binded) {
            // Unbind Service
            this.unbindService(weatherServiceConnection);
            binded = false;
            Utils.log("Service Stopped For Network");
            editTextIpAddress.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
            buttonDisconnect.setEnabled(false);
        }




    }

    private void startServerService() {

        // Create Intent object for WeatherService.
        Intent intent = new Intent(this, MainServiceForClient.class);
        // Call bindService(..) method to bind service with UI.
        intent.putExtra(Constant.IP_ADDRESS_KEY , ipAddress);
        this.bindService(intent, weatherServiceConnection, Context.BIND_AUTO_CREATE);
        Utils.log("Server Service Starting...");

        editTextIpAddress.setVisibility(View.GONE);
        buttonConnect.setVisibility(View.GONE);
        buttonDisconnect.setEnabled(true);
    }




    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        manageServerService(isConnected);
    }
}
