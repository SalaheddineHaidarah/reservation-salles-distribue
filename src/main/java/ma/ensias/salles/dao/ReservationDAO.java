package ma.ensias.salles.dao;

import ma.ensias.salles.db.DBConnection;
import ma.ensias.salles.model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean isAvailable(int salleId, String date, String debut, String fin) {
        // overlap if NOT( fin<=existingStart OR debut>=existingEnd )
        String sql =
                "SELECT COUNT(*) AS c " +
                "FROM reservations " +
                "WHERE salle_id = ? AND date_reservation = ? " +
                "AND NOT ( ? <= heure_debut OR ? >= heure_fin )";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, salleId);
            ps.setString(2, date);   // MySQL will convert from 'YYYY-MM-DD'
            ps.setString(3, fin);    // 'HH:mm' ok for TIME
            ps.setString(4, debut);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c") == 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createReservation(int salleId, String user, String date, String debut, String fin) {
        String sql =
                "INSERT INTO reservations(utilisateur, salle_id, date_reservation, heure_debut, heure_fin) " +
                "VALUES(?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user);
            ps.setInt(2, salleId);
            ps.setString(3, date);
            ps.setString(4, debut);
            ps.setString(5, fin);

            int rows = ps.executeUpdate();
            return rows == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Reservation> findByUser(String user) {
        List<Reservation> list = new ArrayList<>();

        String sql =
                "SELECT id, utilisateur, salle_id, date_reservation, heure_debut, heure_fin " +
                "FROM reservations WHERE utilisateur = ? ORDER BY date_reservation, heure_debut";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setUtilisateur(rs.getString("utilisateur"));
                    r.setSalleId(rs.getInt("salle_id"));
                    r.setDate(rs.getString("date_reservation"));
                    r.setHeureDebut(rs.getString("heure_debut").substring(0,5));
                    r.setHeureFin(rs.getString("heure_fin").substring(0,5));
                    list.add(r);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql =
                "SELECT id, utilisateur, salle_id, date_reservation, heure_debut, heure_fin " +
                "FROM reservations ORDER BY date_reservation, heure_debut";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setUtilisateur(rs.getString("utilisateur"));
                r.setSalleId(rs.getInt("salle_id"));
                r.setDate(rs.getString("date_reservation"));
                r.setHeureDebut(rs.getString("heure_debut").substring(0,5));
                r.setHeureFin(rs.getString("heure_fin").substring(0,5));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
