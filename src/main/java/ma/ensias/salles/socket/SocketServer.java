package ma.ensias.salles.socket;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static final int PORT = 9000;

    public static void main(String[] args) {
        System.out.println("Socket Server starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket Server started on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress() + ":" + client.getPort());

                Thread t = new Thread(new ClientHandler(client));
                t.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
