package service;

import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import model.*;

public class UserService {
    UserDataAccess userDB;
    AuthService aServe;
    public UserService(UserDataAccess userDB, AuthService aServe){
        this.userDB = userDB;
        this.aServe = aServe;
    }

    public LoginResult register(UserData user) throws DataAccessException {
        try{
            userDB.addUser(user);
           LoginRequest userSesh = new LoginRequest(user.username(), user.password());
           AuthData auth = aServe.createAuth(userSesh.username());
            LoginResult result = new LoginResult(auth.username(), auth.authToken());
            return result;
        } catch (UserException e){
            throw new UserException(e.getMessage());
        }

    }

    public AuthData login(LoginRequest user, String authToken) throws DataAccessException {
        if(aServe.getAuth(authToken) != null ){
            throw new AuthException("Error: bad request");
        }
        if(user.username() == null){
            throw new UserException("Error: bad request");
        }
        try {
            UserData existUser = userDB.getUser(user.username());
            if(existUser == null){
                throw new AuthException("Error: There is no account with that username");
            } else if (user.password()==null) {
                throw new UserException("Error: bad Request");
            } else if(!existUser.password().equals(user.password())){
                throw new AuthException("Error: Incorrect Password");
            }
            AuthData userAuth = aServe.createAuth(user.username());
            return userAuth;
        }catch (UserException error){
            throw new UserException(error.getMessage());
        }catch(AuthException e){
            throw new AuthException(e.getMessage());
        }catch (DataAccessException e){
            throw new DataAccessException(e.getMessage());
        }
        }

    public void logOut(String authData) throws DataAccessException {
        try {
            aServe.deleteAuth(authData);
        } catch (UserException error){
            throw new UserException(error.getMessage());
        }catch(AuthException e){
            throw new AuthException(e.getMessage());
        }catch (DataAccessException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    public void deleteUser(String username) {
        try {
            userDB.deleteUser(username);
        } catch (DataAccessException e) {
            System.out.print("beans");
        }
    }

    public void clear() throws DataAccessException {
        try {
            userDB.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
