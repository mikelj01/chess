package dataaccess;

import model.AuthData;
import model.LoginRequest;

public class MemAuthDA implements AuthDataAccess{
    @Override
    public AuthData createAuth(LoginRequest user) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }
}
