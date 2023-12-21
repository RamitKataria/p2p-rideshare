package centralServer;

import server.EchoWorker;
import server.NioServer;
import server.ServerInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class centralServer extends NioServer{
    private HashMap<String,ServerInfo> addressMap;

    // Time out frequency for client server
    public static int Default_timeout_period = 5;
    public int Default_timeout_counter = 3;

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

    private boolean heartbeat() {
        if(!addressMap.isEmpty()) {
            for (Map.Entry<String, ServerInfo> entry : addressMap.entrySet()) {
                String serverName = entry.getKey();
                ServerInfo serverInfo = entry.getValue();
                if (serverInfo.getHeartbeats() > Default_timeout_counter) {
                    addressMap.remove(serverName);
                }else{
                    addressMap.get(serverName).incrementHeartbeats();
                }
            }
        }
        return true;
    }

    private void calculateDistance(InetAddress ipAddress1,InetAddress ipAddress2){

    }

    private void read(SocketChannel clientChannel, ByteBuffer buffer){
        while(buffer.hasRemaining()){
            // Read the fixed size bytes which indicates the type of request
            byte b = buffer.get();

            switch(b){
                // Send list of active Ip addresses
                case 0x1A: {
                    ByteBuffer bufferout = ByteBuffer.allocate(1024);
                    // Number of active Ip addresses
                    bufferout.putInt(addressMap.size());

                    // Loop through the active Ip map
                    for(Map.Entry<String,ServerInfo> entry : addressMap.entrySet()){
                        ServerInfo driverIP = entry.getValue();
                        bufferout.put(driverIP.getIPAddress().getAddress());
                        bufferout.putInt(driverIP.getPort());
                    }
                    this.send(clientChannel, bufferout.array());
                    break;
                }
                // Handle heartbeat messages
                case 0x1B: {
                    InetAddress clientAddress = clientChannel.socket().getInetAddress();
                    int clientPort = buffer.getInt();
                    StringBuilder clientName = new StringBuilder();
                    while(buffer.hasRemaining()){
                        clientName.append(buffer.getChar());
                    }
                    // Check if the rider exists in the map
                    ServerInfo clientInfo = new ServerInfo(clientAddress,clientPort);
                    if(!addressMap.containsKey(clientName.toString())){
                        break;
                    }

                    // Reset heartbeat counter
                    addressMap.get(clientName.toString()).resetHeartbeats();
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
            centralServer server = new centralServer(null,4000, worker);
            new Thread(server).start();

            while(true){
                if(server.heartbeat()){
                    Thread.sleep(Default_timeout_period * 1000L);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}