package ma.ensias.salles.ws.soap;

import javax.jws.WebService;
import ma.ensias.salles.dao.UserDAO;
import ma.ensias.salles.model.User;

@WebService(endpointInterface = "ma.ensias.salles.ws.soap.UserService")
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return userDAO.validateCredentials(username, password);
    }

    @Override
    public boolean register(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (userDAO.findByUsername(username).isPresent()) {
            return false; // User already exists
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole("user");
        return userDAO.create(u);
    }

    @Override
    public boolean userExists(String username) {
        return username != null && userDAO.findByUsername(username).isPresent();
    }
}
