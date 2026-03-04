package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.LogoutRequest;
import model.UserData;


public class UserService {
    UserDataAccess userDB;
    AuthService aServe;
    public UserService(UserDataAccess userDB, AuthService aServe){
        this.userDB = userDB;
        this.aServe = aServe;
    }

    public UserData register(UserData user) throws UserException{
        try {
            UserData existUser = userDB.getUser(user.username());
            if(existUser == null){
                throw new UserException("Something REAALLY Messsed up here: (UserService or UserDataAccess)");
            }
            throw new UserException("That User Already Exists");
        }catch (DataAccessException error){
            try{
                userDB.addUser(user);
                LoginRequest userSesh = new LoginRequest(user.username(), user.password());
                aServe.createAuth(userSesh.username());
                return user;
            } catch (DataAccessException realError){
                throw new UserException("Error accesing the DataBase");
            }
        }

    }
    public AuthData login(LoginRequest user) throws UserException {
        try {
            UserData existUser = userDB.getUser(user.username());
            if(existUser == null){
                throw new UserException("There is no account with that username");
            }
            else if(existUser.password() != user.password()){
                throw new UserException("Incorrect Password");
            }
            AuthData userAuth = aServe.createAuth(user.username());
            return userAuth;
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
        }
        }

    public void logOut(LogoutRequest authData) throws UserException{
        try {
            aServe.deleteAuth(authData.authToken());
        } catch (DataAccessException e) {
            throw new UserException("Error logging Out");
        }
    }
}
