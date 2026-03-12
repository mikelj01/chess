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
//        try {
//            UserData existUser = userDB.getUser(user.username());
//            if(existUser != null){
//                throw new UserException("That User Already Exists");
//            }
//        }catch (UserException error){
//            throw new UserException(error.getMessage());
//        } catch (DataAccessException e) {
//            throw new DataAccessException(e.getMessage());
//        }

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
        try {
            if(aServe.getAuth(authToken) != null ){
                throw new UserException("Error: bad request");
            }
            UserData existUser = userDB.getUser(user.username());
            if(existUser == null){
                throw new UserException("There is no account with that username");
            }
            else if(!existUser.password().equals(user.password())){
                throw new UserException("Incorrect Password");
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

    public void clear(){
        try {
            userDB.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
