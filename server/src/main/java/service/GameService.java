package service;

import dataaccess.GameDataAccess;

public class GameService {
    GameDataAccess gameDB;
    public GameService(GameDataAccess gameDB){
        this.gameDB = gameDB;
    }
}
