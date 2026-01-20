package ma.ensias.salles.socket.rt;

public class NotificationMessage {
    public String type;
    public int salleId;
    public String date;
    public String debut;
    public String fin;
    public String utilisateur;

    public NotificationMessage() {}

    public NotificationMessage(String type, int salleId, String date, String debut, String fin, String utilisateur) {
        this.type = type;
        this.salleId = salleId;
        this.date = date;
        this.debut = debut;
        this.fin = fin;
        this.utilisateur = utilisateur;
    }
}

