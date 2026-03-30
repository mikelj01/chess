package service;

import dataaccess.AuthDataAccess;
import dataaccess.DBException;
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
            throw new UserException("Error: You are Already Logged in");
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            if(authToken == null){
                return null;
            }
            AuthData auth = authDB.getAuth(authToken);
            return auth;
        }catch (DBException e){
            throw new DataAccessException(e.getMessage());
            //throw new AuthException("Error: Unauthorized");
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            AuthData auth = getAuth(authToken);
            if(auth == null){
                throw new AuthException("Error: You are not Logged in");
            }
            authDB.deleteAuth(authToken);
        }catch (AuthException e){
            throw new AuthException(e.getMessage());
        }catch (DataAccessException e){
            throw new DataAccessException(e.getMessage());
        } catch (DBException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try {
            authDB.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
