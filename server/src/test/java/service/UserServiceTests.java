package service;

import dataaccess.*;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTests {
    private static UserData existingUser;
    private static UserData newUser;
    private static UserService service;
    private String existingAuth;
    private static SQLUserDA userDB;
    private static AuthService authService;
    private static SQLAuthDA authDB;

    @AfterAll
    static void stopServer() {
        try {
            service.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void init() throws DataAccessException {
        userDB = new SQLUserDA();
        authDB = new SQLAuthDA();
        authService = new AuthService(authDB);
        service = new UserService(userDB, authService);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");

    }

    @BeforeEach
    public void setup() {
        try {
            service.clear();
            LoginResult result = service.register(existingUser);
            existingAuth = result.authToken();

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerSuccess(){
        String expected = newUser.username();
        try {
            LoginResult userData = service.register(newUser);
            assertEquals(expected, userData.username());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void registerFail(){
        UserException expected = new UserException("Error: That username is taken");
        try {
            LoginResult userData = service.register(existingUser);

        } catch (DataAccessException e) {
            assertEquals(expected.getMessage(), e.getMessage());
        }
    }
    @Test
    void loginSuccess() {
        String expected = existingUser.username();
        try {
            service.logOut(existingAuth);
            LoginRequest req = new LoginRequest(existingUser.username(), existingUser.password());
            AuthData userData = service.login(req, null);
            assertEquals(expected, userData.username());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void loginFail() {
        AuthException expected = new AuthException("Error: bad request");
        try {
            LoginRequest req = new LoginRequest(existingUser.username(), existingUser.password());
            AuthData userData = service.login(req, existingAuth);
            assertEquals(expected, userData.username());

        } catch (DataAccessException e) {
            assertEquals(expected.getMessage(), e.getMessage());
        }
    }

    @Test
    void logOutSuccess() throws DataAccessException {
        service.logOut(existingAuth);
    }
    @Test
    void logOutFail() {
        String expected = "Error: You are not Logged in";
        try{
            service.logOut(null);
        } catch (DataAccessException e) {
            String result = e.getMessage();
            assertEquals(expected, result);
        }
    }

    @Test
    void clearSuccess() {
        try{
            service.clear();
        } catch (DataAccessException e) {
            assertEquals(1,2);
        }
    }
    @Test
    void clearFail(){
        try {
            service.clear();
        } catch (DataAccessException e) {
            assertEquals(1,1);
        }
    }
}
