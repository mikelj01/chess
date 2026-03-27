package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import server.websocket.AuthGenerator;
import service.AuthException;
import service.UserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDA implements UserDataAccess{

    public SQLUserDA() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String userName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT userName, userData FROM users WHERE userName=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteUser(String userName) throws DataAccessException {
        var statement = "DELETE FROM users WHERE userName=?";
        executeUpdate(statement, userName);
    }

    @Override
    public UserData addUser(UserData user) throws DataAccessException {
        String userName = user.username();
        if(getUser(userName) != null){
            throw new UserException("Error: That username is taken");
        }
        if(user.password() == null){
            throw new AuthException("Error: bad Request");
        }
        var statement = "INSERT INTO users (userName, userData) VALUES (?, ?)";
        String userData = new Gson().toJson(user);
        executeUpdate(statement, user.username(), userData);
        return user;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("userData");
        UserData user = new Gson().fromJson(json, UserData.class);
        return user;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
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
            CREATE TABLE IF NOT EXISTS  users (
              userName VARCHAR(255) PRIMARY KEY,
              userData JSON NOT NULL,
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
