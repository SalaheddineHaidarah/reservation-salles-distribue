package ma.ensias.salles.dao;

import ma.ensias.salles.model.Salle;

import java.util.List;

public class TestSalleDAO {

    public static void main(String[] args) {
        System.out.println("TestSalleDAO starting...");
        try {
            SalleDAO dao = new SalleDAO();
            List<Salle> salles = dao.findAll();

            System.out.println("Salles: " + salles.size());
            for (Salle s : salles) {
                System.out.println(s.getId() + " | " + s.getType() + " | " + s.getCapacite() + " | " + s.getEquipements());
            }
        } catch (Exception e) {
            System.err.println("Test failed:");
            e.printStackTrace();
        }
        System.out.println("TestSalleDAO finished.");
    }
}
