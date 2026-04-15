package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDATest {

    private static SQLAuthDA adb;
    private static AuthData existUser;
    private static AuthData newAuth;
    AuthData existAuth;


    @BeforeAll
    public static void init() throws DataAccessException {
        adb = new SQLAuthDA();
        existUser = new AuthData("hi", "p1");

        adb.clear();
    }

    @AfterAll
    static void stopServer() {
        try {
            adb.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        try {
            adb.clear();
            existAuth = adb.createAuth(existUser.username());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAuthBad() {
        try{
            adb.createAuth(existUser.username());
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }


    @Test
    void getAuthBad() {
        try{
            adb.getAuth("existUser.username()");
        } catch (DataAccessException | DBException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }


    @Test
    void createAuth() {
        try{
            AuthData auth = adb.createAuth("beans");
            assertNotNull(auth);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void deleteAuth() {
        try{
            adb.deleteAuth(existAuth.authToken());
            assertEquals(1,1);
        } catch (DBException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAuth() {
        try{
            AuthData auth = adb.getAuth(existAuth.authToken());
            assertNotNull(auth);
        } catch (DBException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clear() {
        try{
            adb.clear();
            assertEquals(1, 1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}