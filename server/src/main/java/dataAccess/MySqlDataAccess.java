package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
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
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUsers(rs).username();
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }


    public void createUser(UserData user) throws DataAccessException {
        if (getUser(user.username()) == null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hashedPassword = encoder.encode(user.password());
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try {
                executeUpdate(statement, user.username(), hashedPassword, user.email());
            } catch (DataAccessException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var users = new ArrayList<UserData>();
                    while (rs.next()) {
                        var user = readUsers(rs);
                        users.add(user);
                    }
                    return users;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public boolean isCorrectPassword(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var correctPassword = readUsers(rs).password();
                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        return encoder.matches(user.password(), correctPassword);
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
            var statement = "SELECT * FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
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
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("row_count");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return 0;
    }

    public boolean checkAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT EXISTS (SELECT 1 FROM auth WHERE authToken=?) AS row_exists;";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBoolean("row_exists");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return false;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";
        try {
            return executeUpdate(statement, gameName, new Gson().toJson(new ChessGame()));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var games = new ArrayList<GameData>();
                    while (rs.next()) {
                        var game = readGame(rs);
                        games.add(game);
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public String updateGame(int gameID, String username, String color) throws DataAccessException, SQLException {
        var game = getGame(gameID);
        if (game != null) {
            if (Objects.equals(color, "WHITE")) {
                if (game.whiteUsername() == null) {
                    var statement = "UPDATE games SET whiteUsername=? WHERE gameID=?;";
                    try {
                        executeUpdate(statement, username, gameID);
                        return "success";
                    } catch (Exception e) {
                        throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                    }
                } else {
                    return "already taken";
                }
            } else if (Objects.equals(color, "BLACK")) {
                if (game.blackUsername() == null) {
                    var statement = "UPDATE games SET blackUsername=? WHERE gameID=?;";
                    try {
                        executeUpdate(statement, username, gameID);
                        return "success";
                    } catch (Exception e) {
                        throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
                    }
                } else {
                    return "already taken";
                }
            }
            return "success";
        } else {
            return "does not exist";
        }
    }
    public void clearDB() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
        statement = "TRUNCATE auth";
        executeUpdate(statement);
        statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    private UserData readUsers(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var v = rs.getString("game");
        var game = new Gson().fromJson(v, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof GameData p) ps.setString(i + 1, new Gson().toJson(p));
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
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) DEFAULT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
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