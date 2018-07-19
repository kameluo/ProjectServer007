package com.example.android.projectserver007;

class Client{
    //=============================ask juan carlos about the static in the 3 variables
    private static String clientIP;
    private static String clientport;
    private static String status;
    private static String soundState;

    public Client(){
                    //suppose the java make the constractor automaticly behind the secne,but i didnt this time
    }

    public Client(String ip,String port){
        clientIP=ip;
        clientport=port;
    }
    public String getClientPort() {
        return clientport;
    }
    public String getClientIP() {
        return clientIP;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public void setSoundState(String status) {
        this.soundState = soundState;
    }
    public String getSoundState() {
        return soundState;
    }
}
