package service;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDA;
import dataaccess.SQLGameDA;
import dataaccess.SQLUserDA;
import model.GameData;
import model.JoinRequest;
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
    private static GameService service;
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
        service = new GameService(gameDB, authService);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        String auth = authService.createAuth(existingUser.username()).authToken();
    }

    @BeforeEach
    public void setup() {
        try {
            service.clear();
            GameData result = service.newGame("game1", existingAuth);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void newGame() {
            try{
                service.newGame("Beans", existingAuth);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
    }


    @Test
    void joinGame() {
        try {
            JoinRequest req = new JoinRequest("WHITE", 1);
            service.joinGame(existingAuth, req);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
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