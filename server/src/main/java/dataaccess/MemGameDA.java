package dataaccess;

import model.AuthData;
import model.GameData;

public class MemGameDA implements GameDataAccess {


    @Override
    public void deleteGame(int gameID) throws DataAccessException {

    }

    @Override
    public GameData crateGame(GameData game) throws DataAccessException {
        return null;
    }

    @Override
    public GameData joinGame(int gameID, AuthData auth) throws DataAccessException {
        return null;
    }
}
