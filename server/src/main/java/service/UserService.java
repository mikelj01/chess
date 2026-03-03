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
    AuthDataAccess authDB;
    public UserService(UserDataAccess userDB, AuthDataAccess authDB){
        this.userDB = userDB;
        this.authDB = authDB;
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
                authDB.createAuth(userSesh);
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
            AuthData userAuth = authDB.createAuth(user);
            return userAuth;
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
        }
        }

    public void logOut(LogoutRequest authData) throws UserException{
        try {
            authDB.deleteAuth(authData.authToken());
        } catch (DataAccessException e) {
            throw new UserException("Error logging Out");
        }
    }
}
