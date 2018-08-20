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
    static TextView textViewNumberOfClients;
    ImageView imageView;
    LinearLayout linearLayoutPrimary;
    LinearLayout linearLayoutSecondaryTexts;
    LinearLayout linearLayoutSecondaryVideoView;
    ToggleButton toggleButtonClients;
    ToggleButton toggleButtonCamera;

    Dialog dialog;
    Button buttonAbout;
    MulticastthreadRun multicastthreadRun=new MulticastthreadRun();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewNumberOfClients = (TextView) findViewById(R.id.NumberOfClients);
        textViewNumberOfClients.setVisibility(View.GONE);
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

    public void toggleButtonConnectToClientsFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            //check the client array number
            //textViewNumberOfClients.setText(" Client/s Connected");
            textViewNumberOfClients.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            linearLayoutSecondaryTexts.setVisibility(View.VISIBLE);

            Intent i=new Intent(this,ClientConnectService.class);
            startService(i);


            PassMessageToMainActivity passMessageToMainActivity=new PassMessageToMainActivity(mHandler);
            passMessageToMainActivity.start();



            runOnUiThread(new Runnable() {
                int counterClients=0;
                @Override
                public void run() {
                    //in this case we can change the user interface
                    counterClients=BroadCastServer.ClientIpArrayList.size();
                    textViewNumberOfClients.setText(counterClients+ " Client/s Connected");
                }
            });



            /*TextView textView=new TextView(MainActivity.this);
            linearLayoutSecondaryTexts.addView(textView);
            textView.setText("hello");

                /*
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Multithread here
                        //multicastthreadRun.run();
                        for(int counter=0;counter<multicastthreadRun.ClientIpArrayList.size();counter++) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //in this case we can change the user interface
                                TextView textView=new TextView(MainActivity.this);
                                linearLayoutSecondaryTexts.addView(textView);
                                textView.setText("hello");
                            }
                        });
                        }//end of the for loop
                    }
                });t.start();
                */
            toggleButtonCamera.setVisibility(View.VISIBLE);
        } else {

            Toast.makeText(getApplicationContext(), "No Clients Connected", Toast.LENGTH_SHORT).show();
            textViewNumberOfClients.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            //toggleButtonCamera.setVisibility(View.v);
            linearLayoutSecondaryTexts.setVisibility(View.GONE);
        }
    }

private final Handler mHandler = new Handler(){
        public void handleMessage(Message msg){

            switch (msg.what){
                case MessageRead:
                    String x=msg.getData().getString("data");
                    TextView textView=new TextView(MainActivity.this);
                    linearLayoutSecondaryTexts.addView(textView);
                    textView.setText(x);
                    break;
            }
        }
};













    public void toggleButtonConnectCamerasFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        VideoView videoView = new VideoView(this, null);
        //should we put a thread for starting and stoping many cameras ??? --->>>> Ask juan carlos
        int numberOfClients = multicastthreadRun.ClientIpArrayList.size();

        if (checked) {
            toggleButtonClients.setEnabled(false);
            for (int counter = 0; counter < numberOfClients; counter++) {
                String clientIp = multicastthreadRun.ClientIpArrayList.get(counter).getClientIP();
                String stringIP = "rtsp://"+clientIp+":8080/h264_pcm.sdp";//camera stream
                //connect the cameras
                linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(stringIP);
                videoView.setVideoURI(uri);
                videoView.start();
            }
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

