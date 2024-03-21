package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.*;
import service.*;

import java.sql.SQLException;

public class ServiceTests {
    private final MemoryDataAccess data = new MemoryDataAccess();
    private final UserService uservice = new UserService(data);
    private final DataService dservice = new DataService(data);

    private final GameService gservice = new GameService(data);

    public ServiceTests() throws Exception {
    }


    @BeforeEach
    void clear() throws DataAccessException {
        dservice.clearDB();
    }

    @Test
    @DisplayName("clear test")
    void clearTest() throws DataAccessException {
        var user1 = new UserData("hello", "goodbye", "hello@goodbye.com");
        uservice.registerUser(user1);
        var user2 = new UserData("hola", "goodbye", "hello@goodbye.com");
        uservice.registerUser(user2);
        var user3 = new UserData("hallo", "goodbye", "hello@goodbye.com");
        uservice.registerUser(user3);
        dservice.clearDB();
        var users = uservice.listUsers();
        Assertions.assertEquals(0, users.size());
    }


    @Test
    @DisplayName("positive register test")
    void registerNewUser() throws DataAccessException {
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        uservice.registerUser(user);
        var users = uservice.listUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertNotNull(data.getUser(username));
    }

    @Test
    @DisplayName("negative register test")
    void registerExistingUser() throws DataAccessException {
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        var user2 = new UserData(username, "goodbye", "hello@goodbye.com");
        uservice.registerUser(user);
        uservice.registerUser(user2);
        var users = uservice.listUsers();
        Assertions.assertTrue(users.size() < 2);
        Assertions.assertNotNull(data.getUser(username));
    }

    @Test
    @DisplayName("positive login test")
    void loginExistingUser() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        Assertions.assertEquals(authToken.getClass(), String.class);

        uservice.logout(authToken);
        var authData2 = uservice.login(user);
        Assertions.assertNotNull(uservice.listUsers());
        Assertions.assertEquals(1, uservice.listUsers().size());
    }

    @Test
    @DisplayName("negative login test")
    void loginNewUser() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        uservice.login(user);
        Assertions.assertEquals(0, uservice.listUsers().size());
    }

    @Test
    @DisplayName("positive logout test")
    void logoutUser() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        uservice.logout(authToken);
        Assertions.assertEquals(1, uservice.listUsers().size());
        Assertions.assertEquals(0, uservice.authSize());
    }

    @Test
    @DisplayName("negative logout test")
    void logoutUserTwice() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        var message1 = uservice.logout(authToken);
        var message2 = uservice.logout(authToken);
        Assertions.assertEquals(1, uservice.listUsers().size());
        Assertions.assertEquals(0, uservice.authSize());
        Assertions.assertNotSame("unauthorized", message1);
        Assertions.assertNotSame("success", message2);
    }

    @Test
    @DisplayName("positive createGame test")
    void createGame() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        var gameData = gservice.createGame(authToken, "afternoon");
        Assertions.assertEquals(1, gservice.listGames(authToken).size());
    }

    @Test
    @DisplayName("negative createGame test")
    void createGameBadAuth() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var users = uservice.listUsers();
        Assertions.assertFalse(users.contains(user.username()));

        gservice.createGame("blah blah blah", "afternoon");
        System.out.print(gservice.listGames("blah blah blah"));
        Assertions.assertNull(gservice.listGames("blah blah blah").get("games"));
    }

    @Test
    @DisplayName("positive listGames test")
    void listGames() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        gservice.createGame(authToken, "grover");
        Assertions.assertNotNull(gservice.listGames(authToken).get("games"));
        Assertions.assertEquals("grover", gservice.listGames(authToken).get("games").getFirst().gameName());

        gservice.createGame(authToken, "grover2");
        Assertions.assertEquals(2, gservice.listGames(authToken).get("games").size());
    }

    @Test
    @DisplayName("negative listGames test")
    void listGamesBadAuth() throws DataAccessException {
        gservice.createGame("false token", "grover");
        Assertions.assertNull(gservice.listGames("false token").get("games"));
    }

    @Test
    @DisplayName("positive joinGame test")
    void joinGame() throws DataAccessException, SQLException {

        // first player
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        var gameID = gservice.createGame(authToken, "grover");
        gservice.joinGame(authToken, gameID, "WHITE");
        Assertions.assertEquals(user.username(), gservice.listGames(authToken).get("games").getFirst().whiteUsername());

        // second player
        var user2 = new UserData("hola", "goodbye", "hello@goodbye.com");
        var authToken2 = uservice.registerUser(user2);
        gservice.joinGame(authToken2, gameID, "BLACK");
        Assertions.assertEquals(user2.username(), gservice.listGames(authToken2).get("games").getFirst().blackUsername());

        // observer
        var user3 = new UserData("hallo", "goodbye", "hello@goodbye.com");
        var authToken3 = uservice.registerUser(user3);
        gservice.joinGame(authToken3, gameID, null);
        Assertions.assertNotNull(gservice.listGames(authToken3).get("games"));
    }

    @Test
    @DisplayName("negative joinGame test")
    void joinGameWrongColor() throws DataAccessException, SQLException {
        // first player
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authToken = uservice.registerUser(user);
        var gameID = gservice.createGame(authToken, "grover");
        gservice.joinGame(authToken, gameID, "WHITE");

        // second player
        var user2 = new UserData("hola", "goodbye", "hello@goodbye.com");
        var authToken2 = uservice.registerUser(user2);
        var message2 = gservice.joinGame(authToken2, gameID, "WHITE");
        Assertions.assertNotSame(user2.username(), gservice.listGames(authToken2).get("games").getFirst().whiteUsername());
        Assertions.assertSame("already taken", message2);
    }

}
