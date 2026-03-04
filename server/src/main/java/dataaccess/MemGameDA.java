package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class MemGameDA implements GameDataAccess {

    ArrayList<GameData> games;

    public MemGameDA(){
        this.games = new ArrayList<>();
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
    games.remove(gameID-1);
    }

    @Override
    public GameData crateGame(GameData game) throws DataAccessException {
        games.add(game);
        return game;
    }

    @Override
    public GameData joinGame(String color, int gameID, AuthData auth) throws DataAccessException {
        try{
            if(color == "White"){
            GameData game = games.get(gameID-1);
            if(game.whiteUsername() != null){
                throw new DataAccessException("That seat is already taken");
            }
            GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            games.set(gameID-1, newGame);
            return newGame;
        }
            GameData game = games.get(gameID-1);
            if(game.blackUsername() != null){
                throw new DataAccessException("That seat is already taken");
            }
            GameData newGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            games.set(gameID-1, newGame);
            return newGame;
        }catch(IndexOutOfBoundsException e){
            throw new DataAccessException("That game doesn't exist");
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        if(games.size()==0){
            throw new DataAccessException("There are no Games");
        }
        return games;
    }

    public void clear(){
        games.clear();
    }
}
