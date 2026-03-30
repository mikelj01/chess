package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import server.websocket.AuthGenerator;
import service.AuthException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SQLAuthDA implements AuthDataAccess{

    public SQLAuthDA() throws DataAccessException {
        configureDatabase();
    }


    @Override
    public AuthData createAuth(String userName) throws DataAccessException {
        if(userName == null|| userName.isBlank()){
            throw new AuthException("Error: Bad request");
        }
        try {
            AuthData auth = AuthGenerator.genAuth(userName);
            var statement = "INSERT INTO auth (authToken, authData) VALUES (?, ?)";
            String authData = new Gson().toJson(auth);
            executeUpdate(statement, auth.authToken(), authData);
            return auth;
        } catch (Exception e) {
            throw new DataAccessException("Error:" + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, DBException {
        try {
            var statement = "DELETE FROM auth WHERE authToken=?";
            executeUpdate(statement, authToken);
        }catch (DataAccessException e){
            throw new DBException("Error:" + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, DBException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, authData FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if(rs == null){
                        throw new DBException("Error: Unauthorized");
                    }
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch(DBException e){
            throw new DBException("Error: Unauthorized");
        }
        catch (Exception e) {
            throw new DataAccessException("Error:" + e.getMessage());
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE auth";
            executeUpdate(statement);
        }catch (Exception e){
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("authData");
        AuthData auth = new Gson().fromJson(json, AuthData.class);
        return auth;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        if(statement == null){
            throw new DataAccessException("This is really bad");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {ps.setString(i + 1, p);}
                    else if (param instanceof Integer p) {ps.setInt(i + 1, p);}
                    else if (param instanceof AuthData p) {ps.setString(i + 1, p.toString());}
                    else if (param == null) {ps.setNull(i + 1, NULL);}
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
            CREATE TABLE IF NOT EXISTS  auth (
              authToken VARCHAR(255) PRIMARY KEY,
              authData JSON NOT NULL,
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
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
