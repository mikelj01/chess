package service;

import dataaccess.AuthDataAccess;

public class AuthService {
    AuthDataAccess authDB;
    public AuthService(AuthDataAccess authDB){
        this.authDB = authDB;
    }
}
