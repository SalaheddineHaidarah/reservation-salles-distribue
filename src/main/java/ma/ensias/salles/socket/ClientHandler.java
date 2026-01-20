package ma.ensias.salles.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private PrintWriter out;
    private volatile boolean running = true;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            out.flush();
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter outWriter = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = outWriter;
            String request = in.readLine(); // one line from client
            if (request == null) {
                outWriter.println("ERROR empty request");
                return;
            }

            // Very simple protocol for now
            if (request.startsWith("CONFIRM")) {
                outWriter.println("OK confirmation received: " + request);
            } else {
                outWriter.println("OK received: " + request);
            }

            // Keep the connection open for real-time updates
            while (running && !socket.isClosed()) {
                Thread.sleep(1000); // Idle loop, could be improved
            }
        } catch (Exception e) {
            // keep it simple for the project
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            SocketServer.removeClient(this);
        }
    }
}
