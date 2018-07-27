package com.example.android.projectserver007;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ClientConnectService extends IntentService{

    MulticastthreadRun multicastthreadRun=new MulticastthreadRun();
    public ClientConnectService() {
        super("My Service");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
        multicastthreadRun.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
    }
}
