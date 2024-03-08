package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Objects;

public class DatabaseTests {

    private final MySqlDataAccess data = new MySqlDataAccess();

    public DatabaseTests() throws Exception {
    }

    @BeforeEach
    @DisplayName("positive clear test")
    void clear() throws DataAccessException {
        data.clearDB();
    }

    @Test
    @DisplayName("positive getUser test")
    void getUser() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        Assertions.assertNotNull(data.getUser("username"));
    }

    @Test
    @DisplayName("negative getUser test")
    void getNonexistentUser() throws DataAccessException {
        Assertions.assertNull(data.getUser("username"));
    }

    @Test
    @DisplayName("positive createUser test")
    void createUser() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        Assertions.assertNotNull(data.getUser("username"));
    }

    @Test
    @DisplayName("negative createUser test")
    void createExistingUser() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertTrue(users.size() < 2);
        Assertions.assertTrue(Objects.equals(users.getFirst().username(), "username"));
    }

    @Test
    @DisplayName("positive listUsers test")
    void listUsers() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertEquals(1, users.size());
    }

    @Test
    @DisplayName("negative listUsers test")
    void listUsersFalseAdd() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertEquals(1, users.size());
        Assertions.assertTrue(Objects.equals(users.getFirst().username(), "username"));
    }

    @Test
    @DisplayName("positive isCorrectPassword test")
    void isCorrectPassword() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var user2 = new UserData("username", "password", "email");
        Assertions.assertTrue(data.isCorrectPassword(user2));
    }

    @Test
    @DisplayName("negative isCorrectPassword test")
    void incorrectPassword() throws DataAccessException {
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var hacker = new UserData("username", "wrong password", "email");
        Assertions.assertFalse(data.isCorrectPassword(hacker));
    }
    @Test
    @DisplayName("positive getAuth test")
    void existingAuth() throws DataAccessException{
        var auth = data.createAuth("username");
        Assertions.assertNotNull(data.getAuth(auth));
    }

    @Test
    @DisplayName("negative getAuth test")
    void nonexistentAuth() throws DataAccessException {
        var auth = data.createAuth("username");
        var falseAuth = "this is a fake token";
        Assertions.assertNull(data.getAuth(falseAuth));
        Assertions.assertNotNull(data.getAuth(auth));
    }

    @Test
    @DisplayName("positive checkAuth test")
    void checkAuth() throws DataAccessException {
        var auth = data.createAuth("username");
        Assertions.assertTrue(data.checkAuth(auth));
    }

    @Test
    @DisplayName("negative checkAuth test")
    void falseAuth() throws DataAccessException {
        var auth = data.createAuth("username");
        var falseAuth = "this is a fake token";
        Assertions.assertFalse(data.checkAuth(falseAuth));
    }

    @Test
    @DisplayName("positive createGame test")
    void createGame() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
    }

    @Test
    @DisplayName("negative createGame test")
    void createGameSameName() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
        Assertions.assertFalse(games.size() == 1);
        Assertions.assertNotSame(gameID, gameID2);
    }

    @Test
    @DisplayName("positive listGames test")
    void listGames() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("donkeypie");
        var games = data.listGames();
        Assertions.assertEquals(2, games.size());
        Assertions.assertSame(games.getFirst().gameID(), gameID);
        Assertions.assertSame(games.getLast().gameID(), gameID2);
        Assertions.assertNotSame(gameID, gameID2);
    }

    @Test
    @DisplayName("negative listGames test")
    void listGamesSameName() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertEquals(2, games.size());
        Assertions.assertNotSame(data.getGame(gameID), data.getGame(gameID2));
    }

//    String updateGame(int gameID, String username, String color) throws DataAccessException, SQLException;

    @Test
    @DisplayName("positive updateGame test")
    void updateGame() throws DataAccessException, SQLException {
        var gameID = data.createGame("monkeypie");
        var message = data.updateGame(gameID, "user1", "WHITE");
        Assertions.assertSame("success", message);

        var message2 = data.updateGame(gameID, "user2", "BLACK");
        Assertions.assertSame("success", message2);

        var message3 = data.updateGame(gameID, "user3", null);
        Assertions.assertSame("success", message3);
    }

    @Test
    @DisplayName("negative updateGame test")
    void updateGameWrongColor() throws DataAccessException, SQLException {
        var gameID = data.createGame("monkeypie");
        var message = data.updateGame(gameID, "user1", "WHITE");
        Assertions.assertSame("success", message);

        var message2 = data.updateGame(gameID, "user2", "WHITE");
        Assertions.assertSame("already taken", message2);
    }

}
