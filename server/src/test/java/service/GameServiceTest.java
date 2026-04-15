package service;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDA;
import dataaccess.SQLGameDA;
import dataaccess.SQLUserDA;
import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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


    }

    @BeforeEach
    public void setup() {
        try {
            service.clear();
            authService.clear();
            existingAuth = authService.createAuth("ExistingUser").authToken();
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
        try {
            ArrayList<GameData> games = service.getGames(existingAuth);
            assertFalse(games.isEmpty());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clear() {
        try{
            service.clear();
        } catch (DataAccessException e) {
            assertEquals(1,2);
        }
    }

    @Test
    void deleteGame() {
        try{
            service.deleteGame(1);
            service.deleteGame(1);
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("beans"));
        }
    }

    @Test
    void newGameFail() {
        try{
            service.newGame("game1", existingAuth);
        } catch (Exception e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void joinGameFail() {
        try {
            JoinRequest req = new JoinRequest("WHITE", 1);
            service.joinGame(existingAuth, req);
            service.joinGame(existingAuth, req);
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void getGamesFail() {
        try {
            ArrayList<GameData> games = service.getGames("beans");
            assertFalse(games.isEmpty());
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void deleteGameFail() {
        try{
            service.deleteGame(10000000);
        } catch (Exception e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }
}