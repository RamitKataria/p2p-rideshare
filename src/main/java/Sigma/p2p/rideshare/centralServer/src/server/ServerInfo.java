package server;

import java.net.InetAddress;

public class ServerInfo {
    private InetAddress address;
    private int port;
    private String hostName;

    public ServerInfo(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }
}
