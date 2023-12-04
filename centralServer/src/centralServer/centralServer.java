package centralServer;

import server.EchoWorker;
import server.NioServer;
import server.ServerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class centralServer extends NioServer{
    private HashMap<String,ServerInfo> addressMap;

    public centralServer(InetAddress hostAddress, int port, EchoWorker worker) throws IOException{
        // Call the superclass constructor of NioServer
        super(hostAddress,port,worker);
        // Create a new address map to store any active users
        this.addressMap = new HashMap<String,ServerInfo>();

        // Start the server
        new Thread(this).start();
    }

    private void saveIPAddress(StringBuilder hostName, ServerInfo clientInfo){
        if(!addressMap.containsKey(hostName))addressMap.put(String.valueOf(hostName),clientInfo);
    }

    private void read(SocketChannel clientChannel, ByteBuffer buffer){
        while(buffer.hasRemaining()){
            byte b = buffer.get();

            switch(b){
                // Send list of active Ip addresses
                case 0x1A: {
                    ByteBuffer bufferout = ByteBuffer.allocate(1024);
                    // Number of active Ip addresses
                    bufferout.putInt(addressMap.size());

                    // Loop through the active Ip map
                    for(Map.Entry<String,ServerInfo> entry : addressMap.entrySet()){
                        ServerInfo rider = entry.getValue();
                        bufferout.put(rider.getAddress().getAddress());
                        bufferout.putInt(rider.getPort());
                    }
                    this.send(clientChannel, bufferout.array());
                    break;
                }
                // Handle heartbeat messages
                case 0x1B: {






                    break;
                }
                // Save and acknowledge a new IP address
                case 0x1C: {
                    InetAddress clientAddress = clientChannel.socket().getInetAddress();
                    int clientPort = buffer.getInt();
                    StringBuilder clientName = new StringBuilder();
                    while(buffer.hasRemaining()){
                        clientName.append(buffer.getChar());
                    }

                    // Save the active IP address
                    ServerInfo clientInfo = new ServerInfo(clientAddress,clientPort);
                    saveIPAddress(clientName,clientInfo);

                    // possibility send acknowledgement message
                    ByteBuffer out = ByteBuffer.allocate(1024);

                    this.send(clientChannel, out.array());
                    break;
                }
            }
        }
    }


    public static void main(String args[])
    {
        try {
            EchoWorker worker = new EchoWorker();
            new Thread(worker).start();
            new Thread(new centralServer(null,4000, worker)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}