package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
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

//    void delAuth(String authToken) throws DataAccessException;

//    AuthData getAuth(String authToken) throws DataAccessException;

//    int authSize() throws DataAccessException;

//    boolean checkAuth(String authToken) throws DataAccessException;

    @Test
    @DisplayName("positive createGame test")
    void createGame() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
    }

//    default ArrayList<GameData> listGames() throws DataAccessException {
//        return null;
//    }

//    String updateGame(int gameID, String username, String color) throws DataAccessException, SQLException;

//    void clearDB() throws DataAccessException;


}
