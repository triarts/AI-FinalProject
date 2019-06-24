package com.personalapp.ai_final_project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class volleySender extends Service {
    private Timer timer = new Timer();
    String TAG = "service";
    //int interval = 5*60*1000; // 5 menit
    int interval = 1*1000;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG,"jalan");
                //sendRequestToServer();   //Your code here
            }
        }, 0, interval);//5 Minutes
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

}
