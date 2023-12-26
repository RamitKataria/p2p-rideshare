import javax.net.ssl.*;
import java.io.*;
import java.security.*;

public class SSLClient {

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 12345;

        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverHost, serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);

            // Send a message to the server
            out.println("Hello from the rider!");

            // Read the response from the server
            String serverResponse = in.readLine();
            System.out.println("Message from rider: " + serverResponse);

            sslSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
