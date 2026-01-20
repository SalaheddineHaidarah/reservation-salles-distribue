package ma.ensias.salles.socket.rt;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class NotificationClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = NotificationServer.PORT;

        System.out.println("Connecting to NotificationServer on " + host + ":" + port);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String line;
            ObjectMapper mapper = new ObjectMapper();
            while ((line = in.readLine()) != null) {
                // try to parse JSON, fallback to raw
                try {
                    NotificationMessage msg = mapper.readValue(line, NotificationMessage.class);
                    System.out.println("[NOTIF] type=" + msg.type + " salle=" + msg.salleId + " date=" + msg.date + " " + msg.debut + "-" + msg.fin + " user=" + msg.utilisateur);
                } catch (Exception ex) {
                    System.out.println("[NOTIF] " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
