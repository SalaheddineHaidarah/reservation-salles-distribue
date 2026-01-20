package ma.ensias.salles.dao;

import ma.ensias.salles.db.DBConnection;
import ma.ensias.salles.model.Salle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> findAll() {
        List<Salle> salles = new ArrayList<>();

        String sql = "SELECT id, type, capacite, equipements FROM salles";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Salle s = new Salle();
                s.setId(rs.getInt("id"));
                s.setType(rs.getString("type"));
                s.setCapacite(rs.getInt("capacite"));
                s.setEquipements(rs.getString("equipements"));
                salles.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return salles;
    }

    public Salle findById(int id) {
        String sql = "SELECT id, type, capacite, equipements FROM salles WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Salle s = new Salle();
                    s.setId(rs.getInt("id"));
                    s.setType(rs.getString("type"));
                    s.setCapacite(rs.getInt("capacite"));
                    s.setEquipements(rs.getString("equipements"));
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean create(Salle salle) {
        String sql = "INSERT INTO salles(type, capacite, equipements) VALUES(?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, salle.getType());
            ps.setInt(2, salle.getCapacite());
            ps.setString(3, salle.getEquipements());
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Salle salle) {
        String sql = "UPDATE salles SET type = ?, capacite = ?, equipements = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, salle.getType());
            ps.setInt(2, salle.getCapacite());
            ps.setString(3, salle.getEquipements());
            ps.setInt(4, salle.getId());
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM salles WHERE id = ?";
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
