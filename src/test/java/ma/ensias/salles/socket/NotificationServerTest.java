package ma.ensias.salles.socket;

import ma.ensias.salles.socket.rt.NotificationMessage;
import ma.ensias.salles.socket.rt.NotificationServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationServerTest {

    private static ExecutorService executor;
    private static Future<?> serverFuture;

    @BeforeAll
    public static void startServer() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        serverFuture = executor.submit(NotificationServer.getInstance());
        // give server time to start
        Thread.sleep(500);
    }

    @AfterAll
    public static void stopServer() throws Exception {
        serverFuture.cancel(true);
        executor.shutdownNow();
    }

    @Test
    public void testBroadcast() throws Exception {
        try (Socket socket = new Socket("localhost", NotificationServer.PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // consume greeting
            String greeting = in.readLine();
            assertNotNull(greeting);

            // broadcast a message
            NotificationMessage msg = new NotificationMessage("TEST", 42, "2026-01-20", "10:00", "11:00", "tester");
            NotificationServer.getInstance().broadcast(msg);

            // read incoming JSON
            String line = in.readLine();
            assertNotNull(line);
            assertTrue(line.contains("TEST"));
            assertTrue(line.contains("42"));
        }
    }
}

