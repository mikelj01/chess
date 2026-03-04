package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    AuthDataAccess authDB;
    public AuthService(AuthDataAccess authDB){
        this.authDB = authDB;
    }


    public AuthData createAuth(String username) throws UserException{
        try{
            AuthData auth = authDB.createAuth(username);
            return auth;
        } catch (DataAccessException e) {
            throw new UserException("You are Already Logged in");
        }
    }

    public AuthData getAuth(String authToken) throws UserException {
        try {
            AuthData auth = authDB.getAuth(authToken);
            return auth;
        }catch (DataAccessException e){
            throw new UserException("You are not authorized");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authDB.deleteAuth(authToken);
        }catch (DataAccessException e){
            throw new DataAccessException("You are not Logged in");
        }
    }

    public void clear(){
        authDB.clear();
    }
}
