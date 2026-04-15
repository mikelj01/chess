package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDATest {

    private static GameData existingGame;
    private static GameData newGame;
    private static SQLGameDA gdb;
    private static AuthData existAuth;
    private static AuthData newAuth;



    @BeforeAll
    public static void init() throws DataAccessException {
        gdb = new SQLGameDA();
        ChessGame theGame = new ChessGame();
        existAuth = new AuthData("hi", "p1");
        newAuth = new AuthData("yo", "p2");
        existingGame = new GameData(1, newAuth.username(), existAuth.username(), "game1", theGame);
        newGame = new GameData(2, null, null, "game2", theGame);
        gdb.clear();
    }

    @AfterAll
    static void stopServer() {
        try {
            gdb.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        try {
            gdb.clear();
            gdb.createGame(existingGame);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    void deleteGame() {
        try {
            gdb.deleteGame(1);
            GameData game = gdb.joinGame("white", 1, existAuth);
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void createGame() {
        try {
            gdb.createGame(newGame);
            ArrayList<GameData> games = gdb.listGames();
            assertTrue(games.size()>1);
        } catch (DataAccessException e) {
            throw new RuntimeException("oops");
        }
    }

    @Test
    void joinGame() {
        try {
            gdb.createGame(newGame);
            GameData game = gdb.joinGame("BLACK", 2, existAuth);
            assertNotNull(game);
        }catch (DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void listGames() {
        try{
            ArrayList<GameData> games = gdb.listGames();
            assertFalse(games.isEmpty());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clear() {
        try{
            gdb.clear();
            assertEquals(1, 1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    

    @Test
    void createGameBad() {
        try {
            gdb.createGame(existingGame);
            gdb.createGame(existingGame);
            assertEquals(1, 2);
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void joinGameBad() {
        try {
            gdb.createGame(newGame);
            GameData game = gdb.joinGame("BLACK", 2, existAuth);
            gdb.joinGame("BLACK", 2, newAuth);
            assertNotNull(game);
        }catch (DataAccessException e){
            assertFalse(e.getMessage().isEmpty());
        }
    }


}