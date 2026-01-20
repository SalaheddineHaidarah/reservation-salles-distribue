package ma.ensias.salles.model;
import java.io.Serializable;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String utilisateur;
    private int salleId;
    private String date;       // "YYYY-MM-DD"
    private String heureDebut; // "HH:mm"
    private String heureFin;   // "HH:mm"

    public Reservation() {
    }

    public Reservation(int id, String utilisateur, int salleId,
                       String date, String heureDebut, String heureFin) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.salleId = salleId;
        this.date = date;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public int getSalleId() {
        return salleId;
    }

    public void setSalleId(int salleId) {
        this.salleId = salleId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }
}
