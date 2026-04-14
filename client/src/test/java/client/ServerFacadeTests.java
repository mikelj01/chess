import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    LoginResult authData;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8000);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:8000");
        //facade = new ServerFacade("http://127.0.0.1:8000");
    }

    @BeforeEach
    public void eachTime(){
        facade.delete();
        UserData req = new UserData("existingUser", "password", "EU@email.com");
        authData = facade.register(req);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() {
        facade.Logout(authData.username());
        UserData req = new UserData("player1", "password", "p1@email.com");
        authData = facade.register(req);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerF() {
        facade.Logout(authData.username());
        UserData req = new UserData("player1", "password", "p1@email.com");
        var authData = facade.register(req);
        try{
            facade.register(req);
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("You are already logged in"));
        }
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void LogoutSuccess() {
        facade.Logout(authData.username());
        assertTrue(authData == null);
    }



}
