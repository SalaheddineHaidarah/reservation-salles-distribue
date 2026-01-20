package ma.ensias.salles.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RmiServer {

    public static void main(String[] args) {
        try {
            // Start registry on port 1099
            LocateRegistry.createRegistry(1099);

            // Bind service
            ReservationRMI service = new ReservationRMIImpl();
            Naming.rebind("rmi://localhost:1099/ReservationService", service);

            System.out.println("RMI Server started: ReservationService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
