package server;

import java.net.InetAddress;

public class ServerInfo {
    private InetAddress address;
    private int port;
    private String hostName;
    private int heartbeatCounter;

    public ServerInfo(InetAddress address, int port){
        this.address = address;
        this.port = port;
        this.heartbeatCounter = 0;
    }

    public InetAddress getIPAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    public int getHeartbeats(){
        return heartbeatCounter;
    }

    public void resetHeartbeats(){
        heartbeatCounter = 0;
    }

    public void incrementHeartbeats(){
        heartbeatCounter++;
    }
}
