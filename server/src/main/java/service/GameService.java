package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    GameDataAccess gameDB;
    AuthService aServe;

    public GameService(GameDataAccess gameDB, AuthService aServe){
        this.gameDB = gameDB;
        this.aServe = aServe;
    }

    public GameData newGame (String gameName, String authToken)throws UserException{
        if(aServe.getAuth(authToken)== null){
            throw new UserException("Not authorized");
        }
        try{
            ArrayList<GameData> games = gameDB.listGames();
            for(GameData game: games){
                if(game.gameName() == gameName){
                    throw  new UserException("That Game Name is Taken");
                }
            }
            GameData newGame = new GameData(games.size(),null, null, gameName, new ChessGame());
            gameDB.crateGame(newGame);
        }catch(DataAccessException e){
            throw new UserException("There was an error accessing the Data (In GameService");
        }
        return null;
    }


}
