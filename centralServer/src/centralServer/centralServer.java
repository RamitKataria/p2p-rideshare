package centralServer;

import server.EchoWorker;
import server.NioServer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

public class centralServer extends NioServer{
    private NioServer c_server;
    private HashMap<String,InetAddress> addressMap;

    public centralServer(InetAddress hostAddress, int port, EchoWorker worker) throws IOException{
        super(hostAddress,port,worker);
        this.c_server = new NioServer(hostAddress,port,worker);
        this.addressMap = new HashMap<String,InetAddress>();

        new Thread(c_server).start();
    }

    public void saveIPAddress(){

    }

    public void send(){

    }


    public static void main(String args[])
    {
        try {
            new centralServer(null,4000,new EchoWorker());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}