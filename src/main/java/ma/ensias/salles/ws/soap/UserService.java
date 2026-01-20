package ma.ensias.salles.ws.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface UserService {

    @WebMethod
    boolean authenticate(@WebParam(name = "username") String username,
                         @WebParam(name = "password") String password);

    @WebMethod
    boolean register(@WebParam(name = "username") String username,
                     @WebParam(name = "password") String password);

    @WebMethod
    boolean userExists(@WebParam(name = "username") String username);
}
