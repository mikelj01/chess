package service;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDA;
import dataaccess.SQLGameDA;
import dataaccess.SQLUserDA;
import model.LoginResult;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static UserData existingUser;
    private static UserData newUser;
    private static UserService service;
    private String existingAuth;
    private static SQLGameDA gameDB;
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
        gameDB = new SQLGameDA();
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

    @Test
    void newGame() {
    }

    @Test
    void joinGame() {
    }

    @Test
    void getGames() {
    }

    @Test
    void clear() {
    }

    @Test
    void deleteGame() {
    }

    @Test
    void newGameFail() {
    }

    @Test
    void joinGameFail() {
    }

    @Test
    void getGamesFail() {
    }

    @Test
    void clearFail() {
    }

    @Test
    void deleteGameFail() {
    }
}