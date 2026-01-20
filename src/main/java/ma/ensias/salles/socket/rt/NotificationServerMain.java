package ma.ensias.salles.socket.rt;

public class NotificationServerMain {
    public static void main(String[] args) {
        new Thread(NotificationServer.getInstance()).start();
    }
}

