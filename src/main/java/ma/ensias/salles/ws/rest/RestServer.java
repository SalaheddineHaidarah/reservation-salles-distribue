package ma.ensias.salles.ws.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class RestServer {

    public static final String BASE_URI = "http://localhost:8081/api/";

    public static HttpServer startServer() {
        ResourceConfig rc = new ResourceConfig()
                .packages("ma.ensias.salles.ws.rest");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        HttpServer server = startServer();
        System.out.println("REST Server started: " + BASE_URI);
        System.out.println("Try: " + BASE_URI + "salles/disponible?id=1&date=2026-01-20&debut=10:00&fin=11:00");

        // keep running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            server.shutdownNow();
        }
    }
}
