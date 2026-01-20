package ma.ensias.salles.ws.soap;

import com.sun.net.httpserver.HttpServer;

import javax.xml.ws.Endpoint;
import java.net.InetSocketAddress;
import ma.ensias.salles.config.Ports;

public class SoapServer {

    public static void main(String[] args) throws Exception {

        String host = "localhost";
        int port = Ports.SOAP_PORT; // use shared Ports constant
        String context = "/reservationWS";
        String url = "http://" + host + ":" + port + context;

        // JDK built-in HTTP server (no auth by default)
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.start();

        Endpoint endpoint = Endpoint.create(new ReservationServiceImpl());
        endpoint.publish(server.createContext(context));

        System.out.println("SOAP Server started: " + url);
        System.out.println("WSDL: " + url + "?wsdl");
    }
}
