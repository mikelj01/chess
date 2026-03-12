package dataaccess;

import model.AuthData;
import server.websocket.AuthGenerator;
import service.AuthException;

import java.util.HashMap;
import java.util.Map;

public class MemAuthDA implements AuthDataAccess{
    Map<String, AuthData> authDataMap;
    public MemAuthDA(){
        this.authDataMap = new HashMap<>();
    }

    @Override
    public AuthData createAuth(String userName) throws DataAccessException {
        if(userName == null){
            throw new AuthException(" Bad request");
        }
        AuthData auth = AuthGenerator.genAuth(userName);
        if(!authDataMap.containsKey(auth.authToken())){
        authDataMap.put(auth.authToken(), auth);
        return auth;
        }
        else{
            throw new DataAccessException(" That Auth is already Filled");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(!authDataMap.containsKey(authToken)){
            throw new AuthException(" Unauthorized");
        }
    authDataMap.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if(authToken == null){
            throw new AuthException(" Not Authorized");
        }
        return authDataMap.get(authToken);

    }

    public void clear() throws DataAccessException {

        try {
            authDataMap.clear();
        }catch (Exception e){
            throw new DataAccessException("There was an error Accessing the Database");
        }
    }
}
