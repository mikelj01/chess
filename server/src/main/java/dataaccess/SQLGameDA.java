package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;

public class SQLGameDA implements GameDataAccess{
    @Override
    public void deleteGame(int gameID) throws DataAccessException {

    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        return null;
    }

    @Override
    public GameData joinGame(String color, int gameID, AuthData auth) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
