package dataaccess;


import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
    static UserData getUser(String userName) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteUser(String userName) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    static UserData addUser(UserData user) throws DataAccessException;
    static AuthData createAuth(AuthData auth) throws DataAccessException;
    GameData crateGame(GameData game) throws DataAccessException;


}
