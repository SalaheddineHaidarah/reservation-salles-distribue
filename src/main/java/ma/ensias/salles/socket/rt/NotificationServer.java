package ma.ensias.salles.socket.rt;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationServer implements Runnable {

    public static final int PORT = 9100;

    private static final NotificationServer INSTANCE = new NotificationServer();

    private final Set<PrintWriter> clients = ConcurrentHashMap.newKeySet();
    private final ObjectMapper mapper = new ObjectMapper();

    private NotificationServer() {}

    public static NotificationServer getInstance() {
        return INSTANCE;
    }

    // Call this from ReservationManager later
    public void broadcast(NotificationMessage message) {
        try {
            String json = mapper.writeValueAsString(message);
            for (PrintWriter out : clients) {
                try {
                    out.println(json);
                    out.flush();
                } catch (Exception ignored) {
                    // if a client is dead, ignore for now
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("NotificationServer started on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Notification client connected: " + client.getInetAddress());

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                clients.add(out);

                // Optional: greeting
                out.println("CONNECTED NotificationServer");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
