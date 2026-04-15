package service;

import dataaccess.DataAccessException;
import dataaccess.SQLAuthDA;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private static UserData existingUser;
    private static UserData newUser;
    private String existingAuth;
    private static AuthService service;
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
        authDB = new SQLAuthDA();
        service = new AuthService(authDB);
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");


    }

    @BeforeEach
    public void setup() {
        try {
            service.clear();
            service.clear();
            existingAuth = service.createAuth("ExistingUser").authToken();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createAuth() {
        try {
            AuthData newAuth = service.createAuth("newUser");
            assertNotNull(newAuth);
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAuth() {
        try {
            AuthData newAuth = service.getAuth(existingAuth);
            assertNotNull(newAuth);
        } catch (UserException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteAuth() {
        try {
            service.deleteAuth(existingAuth);
            AuthData newAuth = service.getAuth(existingAuth);
            assertNull(newAuth);
        } catch (UserException e) {
            assertTrue(e.getMessage().isEmpty());
        } catch (DataAccessException e) {
            assertTrue(e.getMessage().isEmpty());
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
    void createAuthFail() {
        try {
            AuthData newAuth = service.createAuth(" ");
            service.createAuth("newUser");

        } catch (UserException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void getAuthFail() {
        try {
            AuthData newAuth = service.getAuth("beans");
            assertNull(newAuth);
        } catch (UserException e) {
            assertFalse(e.getMessage().isEmpty());
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void deleteAuthFail() {
        try {
            service.deleteAuth("beans");
            AuthData newAuth = service.getAuth("beans");
            assertNotNull(newAuth);
        } catch (UserException e) {
            assertFalse(e.getMessage().isEmpty());
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }


}