package com.example.android.projectserver007;

import android.annotation.TargetApi;
import android.app.IntentService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(api = Build.VERSION_CODES.O)
public class ClientConnectService extends Service {

    MulticastthreadRun multicastthreadRun = new MulticastthreadRun();
    BroadCastUDPServer broadCastServer;


    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    public static final String MSG_DATA="msgdata";

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
             Bundle data= msg.getData();
            switch (msg.what) {
                case serverInterface.Server_starting:
                    Toast.makeText(getApplicationContext(), getString(R.string.SERVER_STARTING)+data.getString(MSG_DATA), Toast.LENGTH_LONG).show();
                    break;
                case serverInterface.Server_MessageRead:
                    Toast.makeText(getApplicationContext(), getString(R.string.SERVER_EVENT_MESSAGE)+data.getString(MSG_DATA), Toast.LENGTH_LONG).show();
                    break;
                case serverInterface.Server_CRQ:
                    Toast.makeText(getApplicationContext(), getString(R.string.SERVER_CRQ)+data.getString(MSG_DATA), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "Service Starting", Toast.LENGTH_SHORT).show();
        broadCastServer = new BroadCastUDPServer();
        broadCastServer.start();


        // If we get killed, after returning from here, restart
        return START_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
    }


    public void postNotification(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }

    /////notification part
    private static final String CHANNEL_ID = "channel1";


//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.NOTIFICATION_CHANNEL_NAME);
//            String description = getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION);
//            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public Notification.Builder getChannelNotification(String title, String body){
//
//        return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
//                .setContentText(body)
//                .setContentTitle(title)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setAutoCancel(true)
//                .setWhen(System.currentTimeMillis());
//    }

    public void sendMessage(int type, String text) {
        Message msg = mServiceHandler.obtainMessage();

        msg.what = type;
        Bundle data = new Bundle();
        data.putString(MSG_DATA,text);
        msg.setData(data);
        mServiceHandler.sendMessage(msg);
    }

    public class BroadCastUDPServer extends Thread implements Runnable, serverInterface {
        public static final int SERVICE_UNICAST_PORT = 9000;
        public static final int SERVICE_BROADCAST_PORT = 9999;// receiving port


        public ArrayList<String> ClientsSoundState = new ArrayList<String>();
        public ArrayList<Client> ClientIpArrayList = new ArrayList<Client>();//Array List For Saving The IPs of the Clients

        int serverstate;//flag
        String oldstate = "SD2";


        DatagramSocket datagramSocketBroadcast;
        DatagramSocket datagramSocketUnicast;
        String soundStateMessage;


        @Override
        public void run() {


            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job


            try {
                datagramSocketUnicast = new DatagramSocket(SERVICE_UNICAST_PORT);
                datagramSocketBroadcast = new DatagramSocket(SERVICE_BROADCAST_PORT);
                sendMessage(serverInterface.Server_starting,"OK");
            } catch (SocketException e) {
                e.printStackTrace();
            }


            while (true) {
                try {
                    //Receiving the "CRQ" message from the Client by a Multicast datagram object
                    byte[] byteBroadCast = new byte[3];
                    DatagramPacket datagramPacketBroadCast = new DatagramPacket(byteBroadCast, byteBroadCast.length);
                    datagramSocketBroadcast.receive(datagramPacketBroadCast);
                    String multiMessage = new String(byteBroadCast);
                    //System.out.println(multiMessage);

                    InetAddress clientIP = datagramPacketBroadCast.getAddress();
                    int clientPort = datagramPacketBroadCast.getPort();
                    //System.out.println(clientIP + "<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    //System.out.println(clientPort + "___________________________");
                    setclientIP(clientIP);//Getting the IP of the the received message
                    setclientPort(clientPort);//Getting the Port of the the received message

                    sendMessage(serverInterface.Server_CRQ,"IP="+clientIP.toString()+":"+clientPort);

                    String clientIPString = clientIP.getHostAddress();//converting the IP from Bytes format to String format to access the client IPs Array list
                    String clientPortString = String.valueOf(clientPort);//converting the Port from integer format to String format to access the client IPs Array list
                    System.out.println(multiMessage.equals("CRQ"));
                    //the end of the broadcast
                    System.out.println("after receiving the CRQ");

                    //Sending the log In message to the whole group by a unicast datagram object
                    send(loginMessage, clientIP, clientPort, datagramSocketUnicast);

                    //passing the ClientIP and the Client Port to the client class to use them in the unicast thread later
                    Client clnt = new Client(clientIPString, clientPortString);
                    //check before adding in the Arraylist
                    if (addClient(clnt) > -1) {
                        if (multiMessage.equals("CRQ")) {
                            System.out.println("hello from if condition------------------");
                            clnt.setStatus("1");//the server is ready to receive
                            //thread to start the unicast sending and receiving messages
                            Thread uniCastThread = new Thread(new UniCastThreadRun(clnt));
                            uniCastThread.start();
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//the end of the infinite while ,to be able to wait for many clients
        }

        private int addClient(Client c) {
            //TODO check if the client already exists prior to insert it in the list
            if (!ClientIpArrayList.contains(c)) {//checking if the array list contains that IP address or not,if not we will add it to it
                ClientIpArrayList.add(c);
                return ClientIpArrayList.size();
            } else
                return -1;
        }

        //TODO The UniCast Class
        class UniCastThreadRun implements Runnable, serverInterface {//client
            Client client = null;


            UniCastThreadRun(Client c) {
                client = c;

            }

            //Constructing the date
            DateFormat dateformat = new SimpleDateFormat("dd/MM/yy HH:mm a");//To Set the Format of the Date
            Date currentdate = new Date();//To Get the Current Date

            //handler message preperation


            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                /*
                //creating a log file for the receiver side
                File file=new File(Environment.getExternalStorageDirectory() +"logserver.txt");
                if(!file.exists()){
                    file.mkdirs();

                }
                */
                System.out.println(client.getStatus());
                String state = client.getStatus();
                System.out.println(state.length());
                System.out.println(state.equals("1"));


                while (state.equals("1")) {
                    //Receiving the Sound States
                    String soundStateMessageRecieved = recievemessage(datagramSocketUnicast);
                    System.out.println(datagramSocketUnicast.getPort() + " " + soundStateMessageRecieved);
                    //Sending Acknowledgment to the client to let him know that the server received the Sound State Message
                    send(acknowledgementSoundState, clientIP, getclientPort(), datagramSocketUnicast);//16-7-2018
                    //Identifying the received message
                    String soundState = "";

                    if (soundStateMessageRecieved.equals("SD0")) {
                        soundState = "Speech";//Speech=SD0


                    } else if (soundStateMessageRecieved.equals("SD1")) {
                        soundState = "Alarm";//Alarm=SD1

                        //Notification.Builder builder=BroadCastServer.getChannelNotification("Project Notification",soundState+"-"+getCurrentTimeStapwithTimeOnly());
                        //BroadCastServer.getManager().notify(new Random().nextInt(),builder.build());


                    } else if (soundStateMessageRecieved.equals("SD2")) {
                        soundState = "Silence";//Silence==SD2


                    } else if (soundStateMessageRecieved.equals("DQR")) {
                        //Receiving "DQR" from the client means that he will disconnect
                        //close and disconnect the datagramSocketForUniCast
                        //datagramSocketunicast.close();
                        //datagramSocketunicast.disconnect();


                        //===================check this step with juan carlos
                        ClientIpArrayList.remove(client);//removing the client IP from the ArrayList
                        client.setStatus("0");//setting the flag 0 to not access the if condition again
                    } else {
                        //if the client sends something else rather than the sound states or the disconnect message we will send him "500" message,(-->datagramPacketUnicastunknownCommandMessage6)
                        System.out.println("UnKnown Command !!!");
                        send(unknownCommandMessage, clientIP, clientPort, datagramSocketUnicast);
                    }

                    //String Contains the received sound state,the date, time of receiving it and the IP of the client
                    String currentState = dateformat.format(currentdate) + " " + clientIP + " " + soundState;
                    //creating an string to pass the ip with the sound state to them main activity
                    System.out.println(clientIP.toString() + ":" + soundState);
                    soundStateMessage = clientIP.toString() + ":" + soundState;

                    ClientsSoundState.add(soundStateMessage + "-" + getCurrentTimeStapwithTimeOnly());

                    ////////////////////////////////////////////////////////////////////////////////notificationCall(soundStateMessage);

                    //bundle.putString("data",soundStateMessage);
                    //mHandler.sendMessage(msg);


                    if (!oldstate.equals(soundStateMessageRecieved)) {
                        oldstate = soundStateMessageRecieved;
                        /*
                        //Write the received state in The Log File Of The Server
                        try {
                            FileWriter fileWriterSoundStates=new FileWriter(file,true);
                            fileWriterSoundStates.write(currentState + "\r\n");
                            fileWriterSoundStates.flush();
                            fileWriterSoundStates.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        */


                    }
                }//the end of the attention loop it finishes when the client status goes to 0


            }//the end of the run loop
        }//the end of the UniCastThreadRun class
        /***************************************** The Methods ******************************************************/
        /**
         * Sending Packets Method
         *
         * @param message-the message we want to send to the client side
         * @param IP-in       InetAddress format
         * @param Port-in     integer format
         * @return Null
         */
        public void send(String message, InetAddress IP, int Port, DatagramSocket datagramSocketsending) {
            byte[] buffer = message.getBytes();
            DatagramPacket datagrampacket = new DatagramPacket(buffer, buffer.length, IP, SERVICE_UNICAST_PORT);
            //datagrampacket.setPort();
            try {
                datagramSocketsending.send(datagrampacket);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * message we will receive from the client side
         *
         * @param datagramSocketrecieving of the socket in SocketAddress format
         * @return message received from the client side in string format
         */
        public String recievemessage(DatagramSocket datagramSocketrecieving) {
            byte[] buffer = new byte[3];
            DatagramPacket datagrampacket = new DatagramPacket(buffer, buffer.length);
            try {
                datagramSocketrecieving.receive(datagrampacket);
                broadCastServer.setclientPort(datagrampacket.getPort());
                System.out.println("IP: " + datagrampacket.getAddress().toString() + " PORT:" + datagrampacket.getPort());
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message = new String(buffer);
            InetAddress clientIP = datagrampacket.getAddress();
            setclientIP(clientIP);
            int port = datagrampacket.getPort();
            setclientPort(port);
            return message;
        }

        private InetAddress clientIP;
        private int clientPort;
        private SocketAddress socket;

        /**
         * Getter and Setter IP,Port "for the receiving method" and Socket
         **/
        public void setclientIP(InetAddress clientIP) {
            clientIP = clientIP;
        }

        public InetAddress getclientIP() {
            return clientIP;
        }

        public void setclientPort(int clientIPort) {
            clientPort = clientIPort;
        }

        public int getclientPort() {
            return clientPort;
        }


        public String getCurrentTimeStapwithTimeOnly() {
            return new SimpleDateFormat("HH:mm a").format(new Date());
        }


    }//// end broadcast


}
