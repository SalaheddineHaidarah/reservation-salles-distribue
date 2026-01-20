package ma.ensias.salles.socket;

import ma.ensias.salles.core.ReservationListener;
import ma.ensias.salles.core.ReservationManager;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer implements ReservationListener {

    public static final int PORT = 9000;
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Socket Server starting on port " + PORT + "...");
        SocketServer server = new SocketServer();
        ReservationManager.getInstance().addListener(server);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Socket Server started on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress() + ":" + client.getPort());
                ClientHandler handler = new ClientHandler(client);
                clients.add(handler);
                Thread t = new Thread(handler);
                t.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReservationUpdate(String message) {
        for (ClientHandler handler : clients) {
            handler.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }
}
