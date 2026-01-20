package ma.ensias.salles.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine(); // one line from client
            if (request == null) {
                out.println("ERROR empty request");
                return;
            }

            // Very simple protocol for now
            if (request.startsWith("CONFIRM")) {
                out.println("OK confirmation received: " + request);
            } else {
                out.println("OK received: " + request);
            }

        } catch (Exception e) {
            // keep it simple for the project
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}
