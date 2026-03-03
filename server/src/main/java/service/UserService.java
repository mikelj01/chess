package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.UserData;


public class UserService {
    UserDataAccess userDB;
    public UserService(UserDataAccess userDB){
        this.userDB = userDB;
    }

    public UserData register(UserData user) throws UserException{
        try {
            UserData existUser = userDB.getUser(user.username());
            if(existUser != null){
                throw new UserException("That username is taken");
            }
            else{
                userDB.addUser(user);
                return user;
            }
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
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
            AuthData userAuth = AuthDataAccess.createAuth(user);
            return userAuth;
        }catch (DataAccessException error){
            throw new UserException(error.getMessage());
        }
        }
}
