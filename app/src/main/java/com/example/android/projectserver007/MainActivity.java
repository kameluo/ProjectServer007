package com.example.android.projectserver007;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity  implements serverInterface{
    static TextView textViewSoundState;
    ImageView imageView;
    LinearLayout linearLayoutPrimary;
    LinearLayout linearLayoutSecondaryTexts;
    LinearLayout linearLayoutSecondaryVideoView;
    ToggleButton toggleButtonClients;
    ToggleButton toggleButtonCamera;

    Dialog dialog;
    Button buttonAbout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewSoundState = (TextView) findViewById(R.id.NumberOfClients);
        textViewSoundState.setVisibility(View.VISIBLE);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        linearLayoutPrimary = (LinearLayout) findViewById(R.id.linearLayoutPrimary);
        linearLayoutSecondaryTexts = (LinearLayout) findViewById(R.id.linearLayoutSecondaryTexts);
        linearLayoutSecondaryVideoView = (LinearLayout) findViewById(R.id.linearLayoutSecondaryVideoViews);
        toggleButtonCamera = (ToggleButton) findViewById(R.id.toggleButtonCamera);
        toggleButtonCamera.setVisibility(View.VISIBLE);
        toggleButtonClients = (ToggleButton) findViewById(R.id.toggleButtonClients);

        buttonAbout=(Button) findViewById(R.id.buttonAbout);

        dialog=new Dialog(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //unlock the multicast messaging /* Turn off multicast filter */
        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("mylock");
            lock.acquire();
            lock.release();
        }




    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case serverInterface.MessageRead:
                    String x = msg.getData().getString("date");
                    textViewSoundState.setText(x);
                    break;
            }
        }
    };

    public void toggleButtonConnectToClientsFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            textViewSoundState.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            linearLayoutSecondaryTexts.setVisibility(View.VISIBLE);

            Intent i=new Intent(this,ClientConnectService.class);
            startService(i);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //in this case we can change the user interface
                    //textViewSoundState.setText();
                }
            });


            toggleButtonCamera.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "No Clients Connected", Toast.LENGTH_SHORT).show();
            textViewSoundState.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            linearLayoutSecondaryTexts.setVisibility(View.GONE);
        }
    }

    public void toggleButtonConnectCamerasFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        VideoView videoView = new VideoView(this, null);

        if (checked) {
                toggleButtonClients.setEnabled(false);

                String clientIp ="ggg";// textViewSoundState.ClientIpArrayList.getClientIP();
                String stringIP = "rtsp://"+clientIp+":8080/h264_pcm.sdp";//camera stream
                //connect the cameras
                linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(stringIP);
                videoView.setVideoURI(uri);
                videoView.start();
        } else {
            videoView.suspend();
            linearLayoutSecondaryVideoView.setVisibility(View.GONE);
            toggleButtonClients.setEnabled(true);
        }
    }

    public void about(View v){
        dialog.setContentView(R.layout.about);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

