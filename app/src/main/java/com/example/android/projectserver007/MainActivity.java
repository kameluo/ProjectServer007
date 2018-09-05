package com.example.android.projectserver007;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements serverInterface{
    ImageView imageView;
    LinearLayout linearLayoutPrimary;
    LinearLayout linearLayoutSecondaryVideoView;
    ToggleButton toggleButtonClients;
    ToggleButton toggleButtonCamera;
    ListView listView;
    VideoView videoView;



    Dialog dialog;
    Button buttonAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        linearLayoutPrimary = (LinearLayout) findViewById(R.id.linearLayoutPrimary);
        linearLayoutSecondaryVideoView = (LinearLayout) findViewById(R.id.linearLayoutSecondaryVideoViews);
        linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
        toggleButtonCamera = (ToggleButton) findViewById(R.id.toggleButtonCamera);
        toggleButtonCamera.setVisibility(View.VISIBLE);
        toggleButtonClients = (ToggleButton) findViewById(R.id.toggleButtonClients);
        listView=(ListView) findViewById(R.id.ListView);
        videoView=(VideoView) findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);
        buttonAbout=(Button) findViewById(R.id.buttonAbout);

        dialog=new Dialog(this);

         /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //unlock the multicast messaging /* Turn off multicast filter
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
            lock.acquire();
            lock.release();
        }
        */
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,BroadCastServer.ClientsSoundState);
        arrayAdapter.notifyDataSetChanged();

    }


    public void toggleButtonConnectToClientsFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            imageView.setVisibility(View.GONE);
            Intent i=new Intent(this,ClientConnectService.class);
            startService(i);
                /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //in this case we can change the user interface
                    //textViewSoundState.setText();
                }
            });*/

            toggleButtonCamera.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "No Clients Connected", Toast.LENGTH_SHORT).show();
            imageView.setVisibility(View.VISIBLE);
        }

    }

    public void toggleButtonConnectCamerasFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
       // VideoView videoView = new VideoView(this, null);

        if (checked) {
                toggleButtonClients.setEnabled(false);

                String clientIp ="192.168.1.102";// textViewSoundState.ClientIpArrayList.getClientIP();
                String uriPath="rtsp://"+clientIp+":8080/h264_pcm.sdp";//camera stream
                String uriPath3="https://"+clientIp+":8080/video";
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

    public void about(View v){
        dialog.setContentView(R.layout.about);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

