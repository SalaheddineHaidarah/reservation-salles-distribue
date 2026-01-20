package ma.ensias.salles.rmi;

import ma.ensias.salles.model.Reservation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ReservationRMI extends Remote {

    boolean reserver(int salleId, String user,
                     String date, String hDebut, String hFin)
            throws RemoteException;

    List<Reservation> mesReservations(String user)
            throws RemoteException;
}
