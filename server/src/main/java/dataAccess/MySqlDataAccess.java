package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
//import exception.ResponseException;
import model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Objects;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws Exception {
        configureDatabase();
    }

    public String getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUsers(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }


    public void createUser(String username, String password, String email) throws DataAccessException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, username, hashedPassword, email);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public Collection<String> listUsers() {
        return null;
    }

    public boolean isCorrectPassword(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var correctPassword = readUsers(rs);
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        String hashedPassword = encoder.encode(user.password());
                        if (Objects.equals(correctPassword, hashedPassword)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return false;
    }

    public String genAuth() {
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder authToken = new StringBuilder();
        Random rnd = new Random();
        while (authToken.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * possible.length());
            authToken.append(possible.charAt(index));
        }
        return authToken.toString();
    }

    public String createAuth(String username) throws DataAccessException {
        var authToken = genAuth();
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return authToken;
    }
    public void delAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
//
    public int authSize() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) AS row_count FROM auth";
            var ps = conn.prepareStatement(statement);
            var rs = ps.executeQuery();
            return rs.getInt("row_count");
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }
//
    public boolean checkAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT EXISTS (SELECT 1 FROM auth WHERE authToken=?) AS row_exists;";
            var ps = conn.prepareStatement(statement);
            ps.setString(1, authToken);
            var rs = ps.executeQuery();
            return rs.getBoolean("row_exists");
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }
//
    public int genGameID() {
        var gameID = new Random();
        return gameID.nextInt(10000);
    }

    public int createGame(String gameName, int gameID) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, gameName, game) VALUES (?, ?, ?)";
        try {
            executeUpdate(statement, gameID, gameName, new Gson().toJson(new ChessGame()));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameID;
    }

    public ArrayList<GameData> listGames() {
//        var gameCollection = GameDict.values();
//        return new ArrayList<>(gameCollection);
        return null;

    }

    public String updateGame(int gameID, String username, String color) {
        return "hello";
//        var strGameID = Integer.toString(gameID);
//        if (GameDict.containsKey(strGameID)) {
//            var gameData = GameDict.get(strGameID);
//            if (Objects.equals(color, "WHITE")) {
//                if (gameData.whiteUsername() == null) {
//                    GameDict.put(strGameID, new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
//                } else {
//                    return "already taken";
//                }
//            } else if (Objects.equals(color, "BLACK")) {
//                if (gameData.blackUsername() == null) {
//                    GameDict.put(strGameID, new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
//                } else {
//                    return "already taken";
//                }
//            }
//            return "success";
//        } else {
//            return "does not exist";
//        }
    }
    public void clearDB() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
        statement = "TRUNCATE auth";
        executeUpdate(statement);
        statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    private String readUsers(ResultSet rs) throws SQLException {
        var var = rs.getString("username");
        return new Gson().fromJson(var, String.class);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var var = rs.getString("authToken");
        return new Gson().fromJson(var, AuthData.class);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}