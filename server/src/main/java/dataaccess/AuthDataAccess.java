package dataaccess;

import model.AuthData;
import model.LoginRequest;
import model.UserData;
import server.websocket.AuthGenerator;

public interface AuthDataAccess {
    static AuthData createAuth(LoginRequest user) throws DataAccessException{
        AuthData userAuth = AuthGenerator.genAuth(user.username());
        return userAuth;
    };
    void deleteAuth(String authToken) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
}
