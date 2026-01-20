package ma.ensias.salles.model;

public class Salle {
    private int id;
    private String type;        // petite | moyenne | grande
    private int capacite;
    private String equipements; // ex: "tableau blanc, projecteur"

    public Salle() {
    }

    public Salle(int id, String type, int capacite, String equipements) {
        this.id = id;
        this.type = type;
        this.capacite = capacite;
        this.equipements = equipements;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getEquipements() {
        return equipements;
    }

    public void setEquipements(String equipements) {
        this.equipements = equipements;
    }
}
