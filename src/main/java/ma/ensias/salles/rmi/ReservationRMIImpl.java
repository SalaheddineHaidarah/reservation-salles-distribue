package ma.ensias.salles.rmi;

import ma.ensias.salles.core.ReservationManager;
import ma.ensias.salles.model.Reservation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ReservationRMIImpl extends UnicastRemoteObject implements ReservationRMI {

    private final ReservationManager manager;

    public ReservationRMIImpl() throws RemoteException {
        super();
        this.manager = ReservationManager.getInstance();
    }

    @Override
    public boolean reserver(int salleId, String user, String date, String hDebut, String hFin)
            throws RemoteException {
        return manager.reserver(salleId, user, date, hDebut, hFin);
    }

    @Override
    public List<Reservation> mesReservations(String user) throws RemoteException {
        return manager.getReservationsByUser(user);
    }
}
