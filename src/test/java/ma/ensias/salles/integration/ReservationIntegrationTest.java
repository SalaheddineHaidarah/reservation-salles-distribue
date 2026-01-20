package ma.ensias.salles.integration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * Integration test for reservation and real-time notification.
 * Simulates:
 * 1. User logs in (stubbed)
 * 2. User makes a reservation via REST
 * 3. Backend inserts into MySQL and triggers TCP notification
 * 4. Notification client receives and prints the update
 */
public class ReservationIntegrationTest {
    public static void main(String[] args) throws Exception {
        // 1. Simulate user login (stub)
        String username = "integrationUser";
        String password = "test";
        System.out.println("[TEST] Logging in as user: " + username);
        // (Assume login is successful, or call REST if endpoint exists)

        // 2. Start notification client in background
        CountDownLatch latch = new CountDownLatch(1);
        Thread notifThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 9000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                System.out.println("[TEST] Notification client connected. Waiting for messages...");
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[NOTIFICATION RECEIVED] " + line);
                    if (line.contains("NEW_RESERVATION") && line.contains(username)) {
                        latch.countDown(); // Signal test success
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("[TEST] Notification client error: " + e.getMessage());
            }
        });
        notifThread.setDaemon(true);
        notifThread.start();

        // 3. Make a reservation via REST
        int salleId = 1; // Use a valid salleId from your DB
        String date = "2026-01-20";
        String heureDebut = "10:00";
        String heureFin = "11:00";
        String json = String.format("{\"utilisateur\":\"%s\",\"salleId\":%d,\"date\":\"%s\",\"heureDebut\":\"%s\",\"heureFin\":\"%s\"}",
                username, salleId, date, heureDebut, heureFin);
        System.out.println("[TEST] Sending reservation request via REST...");
        URL url = new URL("http://localhost:8081/api/reservations");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = con.getResponseCode();
        System.out.println("[TEST] REST response code: " + code);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        System.out.println("[TEST] REST response: " + sb.toString());

        // 4. Wait for notification (timeout after 5 seconds)
        if (!latch.await(5, java.util.concurrent.TimeUnit.SECONDS)) {
            System.err.println("[TEST] Notification not received in time!");
        } else {
            System.out.println("[TEST] Notification received successfully!");
        }
        System.out.println("[TEST] Integration test complete.");
    }
}

