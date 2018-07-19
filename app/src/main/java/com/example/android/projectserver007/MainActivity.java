package com.example.android.projectserver007;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.example.android.projectserver007.serverInterface.loginMessage;

public class MainActivity extends AppCompatActivity {
    static TextView textView;
    ImageView imageView;
    LinearLayout linearLayoutPrimary;
    LinearLayout linearLayoutSecondaryTexts;
    LinearLayout linearLayoutSecondaryVideoView;
    ToggleButton toggleButtonClients;
    ToggleButton toggleButtonCamera;


    public static ArrayList<Client> ClientIpArrayList=new ArrayList<Client>();//Array List For Saving The IPs of the Clients,in 25-5-2018 i made it static and ask juan carlos
    static int serverstate;//flag
    static String oldstate="SD2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.NumberOfClients);
        textView.setVisibility(View.INVISIBLE);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        linearLayoutPrimary=(LinearLayout)findViewById(R.id.linearLayoutPrimary);
        linearLayoutSecondaryTexts=(LinearLayout)findViewById(R.id.linearLayoutSecondaryTexts);
        linearLayoutSecondaryVideoView=(LinearLayout)findViewById(R.id.linearLayoutSecondaryVideoViews);
        toggleButtonCamera=(ToggleButton)findViewById(R.id.toggleButtonCamera);
        toggleButtonCamera.setVisibility(View.GONE);
        toggleButtonClients=(ToggleButton)findViewById(R.id.toggleButtonClients);
    }
public static void setSoundState(int clientip,String value){
        textView.post(new Runnable() {
            @Override
            public void run() {

                textView.setText("");
            }
        });


}
    public void toggleButtonConnectToClientsFunction(View view){
        boolean checked=((ToggleButton)view).isChecked();
        if(checked){
            //check the client array number
            textView.setText("Client/s Connected");
            textView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            Thread threadMulticast=new Thread(new MulticastthreadRun());
            threadMulticast.start();



            /////////////////////////////////////////////////the main multicast class
//            Thread threadMulticast=new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//
//
//                    //while(true){
//                    try {
//                        //Receiving the "CRQ" message from the Client by a Multicast datagram object
//                        int portMulticastCast=3456;//receiving port
//                        InetAddress group=InetAddress.getByName("225.4.5.6");//The MultiCast Group
//                        InetSocketAddress mg = new InetSocketAddress(group,portMulticastCast);
//                        //TODO Enter the IP of this PC in the next line
//                        InetSocketAddress is = new InetSocketAddress("192.168.0.104",portMulticastCast);//the IP of this machine
//                        MulticastSocket multicastSocket=new MulticastSocket(is);
//                        NetworkInterface nis = NetworkInterface.getByInetAddress(is.getAddress());
//                        multicastSocket.joinGroup(mg,nis);//subscribing the multicast IP address to that socket,listening to the message
//
//                        byte [] bMulti=new byte[3];
//                        DatagramPacket datagramPacketMulticast=new DatagramPacket(bMulti,bMulti.length);
//                        multicastSocket.receive(datagramPacketMulticast);
//                        String multiMessage=new String(bMulti);
//                        System.out.println(multiMessage);
//
//                        InetAddress clientIP=datagramPacketMulticast.getAddress();
//                        int clientPort=datagramPacketMulticast.getPort();
//                        System.out.println(clientIP+"<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//                        System.out.println(clientPort+"___________________________");
//                        setclientIP(clientIP);//Getting the IP of the the received message
//                        setclientPort(clientPort);//Getting the Port of the the received message
//
//                        String clientIPString=clientIP.getHostAddress();//converting the IP from Bytes format to String format to access the client IPs Array list
//                        String clientPortString=String.valueOf(clientPort);//converting the Port from integer format to String format to access the client IPs Array list
//                        //TODO Enter the IP of this PC in the next line
//                        SocketAddress socket = new InetSocketAddress("192.168.0.104",20002);//creating a scoket but for unicast
//                        System.out.println(multiMessage.equals("CRQ"));
//                        setsocket(socket);
//                        //the end of the broadcast
//                        System.out.println("after receiving the CRQ");
//
//                        //Sending the log In message to the whole group by a unicast datagram object
//                        send(loginMessage,clientIP,clientPort);
//
//                        //passing the ClientIP and the Client Port to the client class to use them in the unicast thread later
//                        Client clnt=new Client(clientIPString,clientPortString);
//                        //check before adding in the Arraylist
//                        if(addClient(clnt)>-1){
//                            if(multiMessage.equals("CRQ")){
//                                System.out.println("hello from if condition------------------");
//                                clnt.setStatus("1");//the server is ready to receive
//                                ///////////thread to start the unicast sending and receiving messages
//                                Thread threadUnicast=new Thread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//
//
//
//                                    }
//                                });
//                                threadUnicast.start();
//                                ///////////end of the unicast thread
//                            }
//                        }
//                    } catch (UnknownHostException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    //}//the end of the infinite while ,to be able to wait for many clients
//                }
//
//                private int addClient(Client c){
//                    //TODO check if the client already exists prior to insert it in the list
//                    if(!ClientIpArrayList.contains(c)){//checking if the array list contains that IP address or not,if not we will add it to it
//                        ClientIpArrayList.add(c);
//                        return ClientIpArrayList.size();
//                    }else
//                        return -1;
//                }
//            });
//            threadMulticast.start();
            //////////////////////////////////////////////////the end of the multicast class

            linearLayoutSecondaryTexts.setVisibility(View.VISIBLE);
            final MulticastthreadRun multicastthreadRun=new MulticastthreadRun();
            final int numberOfClients=multicastthreadRun.ClientIpArrayList.size();

            for(int counter=0;counter<numberOfClients;counter++) {

                //soundstates here
                //linearLayoutSecondaryTexts.addView(new TextView(this));
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //TextView soundState = new TextView(this, null);
                        //soundState.setText(multicastthreadRun.ClientIpArrayList.get(counter).getClientIP() +" Sound State :"+ MulticastthreadRun.clientIP);
                       //


                        // linearLayoutSecondaryTexts.addView(soundState);

runOnUiThread(new Runnable() {
    @Override
    public void run() {
        //in this case we can change the user interface
    }
});


                    }
                });
                t.start();
            }

            toggleButtonCamera.setVisibility(View.VISIBLE);







        }else{

            Toast.makeText(getApplicationContext(), "No Clients Connected", Toast.LENGTH_SHORT).show();
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            toggleButtonCamera.setVisibility(View.GONE);
            linearLayoutSecondaryTexts.setVisibility(View.GONE);
        }
    }

    public void toggleButtonConnectCamerasFunction(View view){
        boolean checked=((ToggleButton)view).isChecked();

        VideoView videoView=new VideoView(this,null);

        //should we put a thread for starting and stoping many cameras ??? --->>>> Ask juan carlos

        MulticastthreadRun multicastthreadRun=new MulticastthreadRun();
        int numberOfClients=multicastthreadRun.ClientIpArrayList.size();

        if(checked){
            toggleButtonClients.setEnabled(false);
            for(int counter=0;counter<numberOfClients;counter++) {
                String clientIp=multicastthreadRun.ClientIpArrayList.get(counter).getClientIP();
                String clientCameraIp=getCameraIP(clientIp);
                 String stringIP="rtsp://"+clientCameraIp+":8080/h264_pcm.sdp";//phone stream
                //connect the cameras
                linearLayoutSecondaryVideoView.setVisibility(View.VISIBLE);
                Uri uri=Uri.parse(stringIP);
                videoView.setVideoURI(uri);
                videoView.start();
            }
        }else{
            videoView.suspend();
            linearLayoutSecondaryVideoView.setVisibility(View.GONE);
            toggleButtonClients.setEnabled(true);
        }
    }


    public String getCameraIP(String clientIp){

        String[] parts = clientIp.split(".");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String part4 = parts[3];
        int clientIPPart4 = Integer.parseInt(part4);
        int cameraIPpart4=clientIPPart4+1;
        String cameraIPLastPart=String.valueOf(cameraIPpart4);

        String cameraIP=part1+"."+part2+"."+part3+"."+cameraIPLastPart;
        return cameraIP;
    }


    /***************************************** The Methods ******************************************************/
    /**
     * Sending Packets Method
     * @param message-the message we want to send to the client side
     * @param IP-in InetAddress format
     * @param Port-in integer format
     * @return Null
     */
    public static void send(String message,InetAddress IP,int Port){
        byte [] buffer=message.getBytes();
        DatagramPacket datagrampacket=new DatagramPacket(buffer,buffer.length,IP,Port);
        datagrampacket.setPort(20002);
        try {
            DatagramSocket datagramSocket=new DatagramSocket();
            datagramSocket.send(datagrampacket);
            datagramSocket.setReuseAddress(true);
            datagramSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * message we will receive from the client side
     * @param socket of the socket in SocketAddress format
     * @return message received from the client side in string format
     */
    public static String recievemessage(SocketAddress socket){
        byte [] buffer=new byte [3];
        DatagramPacket datagrampacket=new DatagramPacket(buffer,buffer.length);
        try {
            DatagramSocket datagramsocket=new DatagramSocket(socket);
            datagramsocket.receive(datagrampacket);
            datagramsocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String message=new String(buffer);
        InetAddress clientIP=datagrampacket.getAddress();
        setclientIP(clientIP);
        int port=datagrampacket.getPort();
        setclientPort(port);
        return message;
    }


    public static InetAddress clientIP;
    private static int clientPort;
    private static SocketAddress socket;
    /** Getter and Setter IP,Port "for the receiving method" and Socket **/
    public static void setclientIP(InetAddress clientIP){
        MulticastthreadRun.clientIP=clientIP;
    }
    public static InetAddress getclientIP(){
        return clientIP;
    }
    public static void setclientPort(int clientIPort){
        MulticastthreadRun.clientPort=clientIPort;
    }
    public static int getclientPort(){
        return clientPort;
    }
    public static void setsocket(SocketAddress socket){
        MulticastthreadRun.socket=socket;
    }
    public static SocketAddress getsocket(){
        return socket;
    }
}
