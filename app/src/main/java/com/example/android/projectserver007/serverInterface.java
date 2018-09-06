package com.example.android.projectserver007;

public interface serverInterface {
    String loginMessage="SON";// "SEVON" is sent to the client as a log in message
    String acknowledgementSoundState="200";//Acknowledgment message sent to the client to let him know that the server received the Sound State Message
    String unknownCommandMessage="500";//if the client sends something else rather than the sound states or the disconnect message we will send him "500" message
    String serverWantsDisconnect="555";//the Server wants to disconnect


    //Server events
    public static final int Server_CRQ=0;
    public static final int messageStateChange = 1;

    public static final int Server_MessageRead=2;
    public static final int Server_starting=3;
}