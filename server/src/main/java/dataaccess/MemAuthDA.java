package dataaccess;

import model.AuthData;
import model.LoginRequest;
import model.UserData;
import server.websocket.AuthGenerator;

import java.util.HashMap;
import java.util.Map;

public class MemAuthDA implements AuthDataAccess{
    Map<String, AuthData> authDataMap;
    public MemAuthDA(){
        this.authDataMap = new HashMap<>();
    }
    @Override
    public AuthData createAuth(LoginRequest user) throws DataAccessException {
        AuthData auth = AuthGenerator.genAuth(user.username());
        authDataMap.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
    authDataMap.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataMap.get(authToken);
    }
}
