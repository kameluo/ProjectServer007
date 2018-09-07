package com.example.android.projectserver007;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements serverInterface {

    LinearLayout linearLayoutPrimary;
    LinearLayout linearLayoutSecondaryVideoView;
    ToggleButton toggleButtonClients;
    ToggleButton toggleButtonCamera;
    static ListView listView;
    VideoView videoView;


    Dialog dialog;

    public static ArrayList<String> ClientsSoundState = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        linearLayoutPrimary = (LinearLayout) findViewById(R.id.linearLayoutPrimary);
        linearLayoutSecondaryVideoView = (LinearLayout) findViewById(R.id.linearLayoutSecondaryVideoViews);
        linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
        toggleButtonCamera = (ToggleButton) findViewById(R.id.toggleButtonCamera);
        toggleButtonCamera.setVisibility(View.VISIBLE);
        toggleButtonClients = (ToggleButton) findViewById(R.id.toggleButtonClients);
        listView = (ListView) findViewById(R.id.ListView);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);


        dialog = new Dialog(this);

       if(isServiceRunning()){
           toggleButtonClients.setChecked(true);
       }




    }
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            Log.d("Services...",service.service.getClassName());
            if("com.example.android.projectserver007.ClientConnectService".equals(service.service.getClassName())) {
                Toast.makeText(this,"Found service",Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent!=null && (intent.getFlags() == (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK))){
            if(toggleButtonClients!=null)
                toggleButtonClients.setChecked(true);
        }
    }

    public void toggleButtonConnectToClientsFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        isServiceRunning();
        if (checked) {

            Intent i = new Intent(this, ClientConnectService.class);
            startService(i);

            //toggleButtonCamera.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getApplicationContext(), "No Clients Connected", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ClientConnectService.class);
            stopService(i);
        }

    }

    public void toggleButtonConnectCamerasFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        // VideoView videoView = new VideoView(this, null);

        if (checked) {
            toggleButtonClients.setEnabled(false);

            String clientIp = "192.168.1.102";// textViewSoundState.ClientIpArrayList.getClientIP();
            String uriPath = "rtsp://" + clientIp + ":8080/h264_pcm.sdp";//camera stream
            String uriPath3 = "https://" + clientIp + ":8080/video";
            //String uriPath2="rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";
            //connect the cameras
            linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
            //Uri uri=Uri.parse(uriPath);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(uriPath));
            videoView.start();
        } else {
            videoView.suspend();
            linearLayoutSecondaryVideoView.setVisibility(View.GONE);
            toggleButtonClients.setEnabled(true);
            videoView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                dialog.setContentView(R.layout.fragment_about);
                dialog.show();
                return true;

            default:
                return false;
        }
    }


    public static class AddClient implements Runnable {

        protected Context mContext;
        protected String mMessage="";
        AddClient(Context c, String message){
            mMessage=message;
            mContext=c;
        }

        @Override
        public void run() {
            ClientsSoundState.add(ClientsSoundState.size()+" "+mMessage);
            ArrayAdapter arrayAdapter=new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,ClientsSoundState);
            if(listView!=null)
                listView.setAdapter(arrayAdapter);
                        //arrayAdapter.notifyDataSetChanged();
        }
    }
}

