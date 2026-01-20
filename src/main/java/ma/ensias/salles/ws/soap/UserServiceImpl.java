package ma.ensias.salles.ws.soap;

import javax.jws.WebService;
import java.util.HashMap;
import java.util.Map;

@WebService(endpointInterface = "ma.ensias.salles.ws.soap.UserService")
public class UserServiceImpl implements UserService {

    // Simple in-memory user storage (username -> password)
    private static final Map<String, String> users = new HashMap<>();

    static {
        // Add some default users
        users.put("admin", "admin123");
        users.put("user1", "pass1");
        users.put("user2", "pass2");
    }

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String storedPassword = users.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    @Override
    public boolean register(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (users.containsKey(username)) {
            return false; // User already exists
        }
        users.put(username, password);
        return true;
    }

    @Override
    public boolean userExists(String username) {
        return username != null && users.containsKey(username);
    }
}

