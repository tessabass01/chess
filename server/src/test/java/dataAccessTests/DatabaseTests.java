package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Objects;

public class DatabaseTests {

    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess db;
        if (databaseClass.equals(MySqlDataAccess.class)) {
            db = new MySqlDataAccess();
        } else {
            db = new MemoryDataAccess();
        }
        db.clearDB();
        return db;
    }

    public DatabaseTests() throws Exception {
    }

    @ParameterizedTest
    @DisplayName("positive clear test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void clear(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        data.clearDB();
    }

    @ParameterizedTest
    @DisplayName("positive getUser test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        Assertions.assertNotNull(data.getUser("username"));
    }

    @ParameterizedTest
    @DisplayName("negative getUser test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getNonexistentUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        Assertions.assertNull(data.getUser("username"));
    }

    @ParameterizedTest
    @DisplayName("positive createUser test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        Assertions.assertNotNull(data.getUser("username"));
    }

    @ParameterizedTest
    @DisplayName("negative createUser test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createExistingUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertTrue(users.size() < 2);
        Assertions.assertTrue(Objects.equals(users.getFirst().username(), "username"));
    }

    @ParameterizedTest
    @DisplayName("positive listUsers test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listUsers(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertEquals(1, users.size());
    }

    @ParameterizedTest
    @DisplayName("negative listUsers test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listUsersFalseAdd(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        data.createUser(user);
        var users = data.listUsers();
        Assertions.assertEquals(1, users.size());
        Assertions.assertTrue(Objects.equals(users.getFirst().username(), "username"));
    }

    @ParameterizedTest
    @DisplayName("positive isCorrectPassword test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void isCorrectPassword(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var user2 = new UserData("username", "password", "email");
        Assertions.assertTrue(data.isCorrectPassword(user2));
    }

    @ParameterizedTest
    @DisplayName("negative isCorrectPassword test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void incorrectPassword(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var user = new UserData("username", "password", "email");
        data.createUser(user);
        var hacker = new UserData("username", "wrong password", "email");
        Assertions.assertFalse(data.isCorrectPassword(hacker));
    }
    @ParameterizedTest
    @DisplayName("positive getAuth test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void existingAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var auth = data.createAuth("username");
        Assertions.assertNotNull(data.getAuth(auth));
    }

    @ParameterizedTest
    @DisplayName("negative getAuth test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void nonexistentAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var auth = data.createAuth("username");
        var falseAuth = "this is a fake token";
        Assertions.assertNull(data.getAuth(falseAuth));
        Assertions.assertNotNull(data.getAuth(auth));
    }

    @ParameterizedTest
    @DisplayName("positive checkAuth test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void checkAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var auth = data.createAuth("username");
        Assertions.assertTrue(data.checkAuth(auth));
    }

    @ParameterizedTest
    @DisplayName("negative checkAuth test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void falseAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var auth = data.createAuth("username");
        var falseAuth = "this is a fake token";
        Assertions.assertFalse(data.checkAuth(falseAuth));
    }

    @ParameterizedTest
    @DisplayName("positive createGame test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
    }

    @ParameterizedTest
    @DisplayName("negative createGame test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createGameSameName(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
        Assertions.assertFalse(games.size() == 1);
        Assertions.assertNotSame(gameID, gameID2);
    }

    @ParameterizedTest
    @DisplayName("positive listGames test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listGames(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("donkeypie");
        var games = data.listGames();
        Assertions.assertEquals(2, games.size());
        Assertions.assertSame(games.getFirst().gameID(), gameID);
        Assertions.assertSame(games.getLast().gameID(), gameID2);
        Assertions.assertNotSame(gameID, gameID2);
    }

    @ParameterizedTest
    @DisplayName("negative listGames test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listGamesSameName(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var gameID2 = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertEquals(2, games.size());
        Assertions.assertNotSame(data.getGame(gameID), data.getGame(gameID2));
    }

    @ParameterizedTest
    @DisplayName("positive updateGame test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void updateGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var message = data.updateGame(gameID, "user1", "WHITE");
        Assertions.assertSame("success", message);

        var message2 = data.updateGame(gameID, "user2", "BLACK");
        Assertions.assertSame("success", message2);

        var message3 = data.updateGame(gameID, "user3", null);
        Assertions.assertSame("success", message3);
    }

    @ParameterizedTest
    @DisplayName("negative updateGame test")
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void updateGameWrongColor(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess data = getDataAccess(dbClass);
        var gameID = data.createGame("monkeypie");
        var message = data.updateGame(gameID, "user1", "WHITE");
        Assertions.assertSame("success", message);

        var message2 = data.updateGame(gameID, "user2", "WHITE");
        Assertions.assertSame("already taken", message2);
    }

}
