package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import service.JoinException;
import service.UserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDA implements GameDataAccess{

    public SQLGameDA() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        try {
            var statement = "DELETE FROM games WHERE id=?";
            executeUpdate(statement, gameID);
        }catch (Exception e){
            throw new DataAccessException("Error:" + e.getMessage());
        }
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        try {
            var statement = "INSERT INTO games (id, gameData) VALUES (?, ?)";
            String json = new Gson().toJson(game);
            executeUpdate(statement, game.gameID(), json);
            return game;
        } catch (Exception e) {
            throw new DataAccessException("Error:" + e.getMessage());
        }
    }

    @Override
    public GameData joinGame(String color, int gameID, AuthData auth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ArrayList<GameData> games = listGames();
            ArrayList<Integer> ids = new ArrayList<>();
            for(GameData game : games){
                ids.add(game.gameID());
            }
            if(!ids.contains(gameID)){
                throw new UserException("Error: bad request");
            }
            GameData game = null;
            var statement = "SELECT id, gameData FROM games WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                         game = readGame(rs);
                    }
                }
            }
            if(Objects.equals(color, "WHITE")  && game != null){
                if(game.whiteUsername() != null){
                    throw new JoinException("Error: That seat is already taken");
                }
                GameData newGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
                statement = "UPDATE games SET gameData = ? WHERE id = ?";
                String gameData = new Gson().toJson(newGame);
                executeUpdate(statement, gameData, newGame.gameID());
                return newGame;
            }

            if(Objects.equals(color, "BLACK") && game != null){
                if(game.blackUsername() != null){
                    throw new JoinException("Error: That seat is already taken");
                }
                GameData newGame = new GameData(game.gameID(),game.whiteUsername(), auth.username() , game.gameName(), game.game());
                statement = "UPDATE games SET gameData = ? WHERE id = ?";
                String gameData = new Gson().toJson(newGame);
                executeUpdate(statement, gameData, newGame.gameID());
                return newGame;
            }
            throw new UserException("Error: Bad Request");
        }catch (JoinException e){
            throw new JoinException(e.getMessage());
        }catch (UserException e){
            throw new UserException(e.getMessage());
        }
        catch (Exception e) {
            throw new DataAccessException("Error:" + e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, gameData FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error:" + e.getMessage());
        }
        return result;
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE games";
            executeUpdate(statement);
        }catch (Exception e){
            throw new DataAccessException("Error: "+e.getMessage());
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("gameData");
        GameData game = new Gson().fromJson(json, GameData.class);
        return game;
    }
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            if(statement == null){
                throw new DataAccessException("This is really bad");
            }
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof AuthData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              id int PRIMARY KEY,
              gameData JSON NOT NULL,
              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("We are unable to configure database: %s", ex.getMessage()));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}

