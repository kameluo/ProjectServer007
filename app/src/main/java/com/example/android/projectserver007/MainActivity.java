package com.example.android.projectserver007;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
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

    }

    public void toggleButtonConnectToClientsFunction(View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            //check the client array number
            textViewNumberOfClients.setText(multicastthreadRun.ClientIpArrayList.size() +" Client/s Connected");
            textViewNumberOfClients.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            linearLayoutSecondaryTexts.setVisibility(View.VISIBLE);

            Intent i=new Intent(this,ClientConnectService.class);
            startService(i);


            //new connectClients().execute();

            //Multithread here
            //multicastthreadRun.run();

                //soundstates here
                //linearLayoutSecondaryTexts.addView(new TextView(this));
            /*  /Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //Multithread here
                        multicastthreadRun.run();
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
    public class connectClients extends AsyncTask<Void, Void, Void> {
        @Override
        public Void doInBackground(Void... voids) {
            //Multithread here
            multicastthreadRun.run();
            return null;
        }
        @Override
        public void onPreExecute(){
            Toast.makeText(getApplicationContext(), "Starting Multicast", Toast.LENGTH_SHORT).show();
        }

        public void onPostExecute(){
            Toast.makeText(getApplicationContext(), "After Multicast", Toast.LENGTH_SHORT).show();
        }
    }





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

