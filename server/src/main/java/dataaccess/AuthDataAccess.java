package dataaccess;

import model.AuthData;
import model.LoginRequest;
import model.UserData;
import server.websocket.AuthGenerator;

public interface AuthDataAccess {
    AuthData createAuth(String userName) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void clear();
}
