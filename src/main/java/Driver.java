import javax.net.ssl.*;
import java.io.*;
import java.security.*;

public class Driver {

    public static void main(String[] args) {
        int port = 12345;

        try {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("Server started. Waiting for a client...");

            SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);

            // Read message from client
            String clientMessage = in.readLine();
            System.out.println("Message from rider: " + clientMessage);

            // Send a response back to the client
            out.println("Hello from the driver!");

            sslSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
