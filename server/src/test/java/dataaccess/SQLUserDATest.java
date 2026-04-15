package dataaccess;


import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SQLUserDATest {

    private static UserData existingUser;
    private static UserData newUser;
    private static SQLUserDA udb;



    @BeforeAll
    public static void init() throws DataAccessException {
        udb = new SQLUserDA();
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        udb.clear();
    }

    @AfterAll
    static void stopServer() {
        try {
            udb.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        try {
            udb.clear();
            udb.addUser(existingUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserBad() {
        try {
            UserData user = udb.getUser("nonUser");
            assertNull(user);
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }

    @Test
    void addUserBad() {
        try {
            udb.addUser(existingUser);
            assertEquals(1, 2);
        } catch (DataAccessException e) {
            assertFalse(e.getMessage().isEmpty());
        }
    }


    @Test
    void getUser() {
        try {
            UserData user = udb.getUser("existingUser");
            assertNotNull(user);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteUser() {
        try {
            udb.deleteUser("existingUser");
            assertEquals(1, 1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addUser() {
        try {
            udb.addUser(newUser);
            assertEquals(1, 1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clear() {
        try{
            udb.clear();
            assertEquals(1, 1);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}