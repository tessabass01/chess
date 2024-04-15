package clientTests;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import model.JoinData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import ui.State;

import java.net.HttpURLConnection;


public class ServerFacadeTests {
    private static ServerFacade serverFacade;

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    @DisplayName("positive clear test")
    public void clear() throws ResponseException {
        serverFacade.clearDB();
    }


    @Test
    @DisplayName("positive addUser test")
    public void addUser() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        serverFacade.logout(authData.authToken());
        Assertions.assertFalse(serverFacade.users.isEmpty());
    }
    @Test
    @DisplayName("negative addUser test")
    public void addUserTwice() throws ResponseException {
        var token = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com")));
    }
    @Test
    @DisplayName("positive login test")
    public void loginUser() throws ResponseException {
        var token = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        Assertions.assertFalse(serverFacade.users.isEmpty());
        Assertions.assertDoesNotThrow(() -> serverFacade.login(new UserData("john", "monkeypie", null)));
    }

    @Test
    @DisplayName("negative login test")
    public void loginNewUser() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new UserData("john", "monkeypie", null)));
    }

    @Test
    @DisplayName("positive logout test")
    public void logout() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        serverFacade.logout(authData.authToken());
        Assertions.assertFalse(serverFacade.users.isEmpty());
    }

    @Test
    @DisplayName("negative logout test")
    public void logoutFaultyToken() throws ResponseException {
        var token = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("fake token"));
    }

    @Test
    @DisplayName("positive createGame test")
    public void createGame() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game, authData.authToken());
        Assertions.assertEquals(1, serverFacade.listGames(authData.authToken()).length);
    }

    @Test
    @DisplayName("negative createGame test")
    public void createGameFaultyToken() throws ResponseException {
        var token = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(game, "fake token"));
    }

    @Test
    @DisplayName("positive joinObserver test")
    public void joinObserver() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game, authData.authToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.joinObserver(String.valueOf(ID.gameID()), authData.authToken()));
    }

    @Test
    @DisplayName("negative joinObserver test")
    public void joinObserverFaultyID() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game, authData.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinObserver(String.valueOf(ID.gameID()), "fake token"));
    }

    @Test
    @DisplayName("positive joinGame test")
    public void joinGame() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game, authData.authToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(String.valueOf(ID.gameID()), "white", authData.authToken()));
    }

    @Test
    @DisplayName("negative joinGame test")
    public void joinGameWrongColor() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game, authData.authToken());
        serverFacade.joinGame(String.valueOf(ID.gameID()), "white", authData.authToken());

        var authData2 = serverFacade.addUser(new UserData("jane", "monkeypie", "donkey@pie.com"));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(String.valueOf(ID.gameID()), "white", authData2.authToken()));
    }

    @Test
    @DisplayName("positive listGame test")
    public void listGame() throws ResponseException {
        var authData = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        var game1 = new GameData(null, null, null, "monkey", new ChessGame());
        var ID = serverFacade.createGame(game1, authData.authToken());
        var game2 = new GameData(null, null, null, "donkey", new ChessGame());
        var ID2 = serverFacade.createGame(game2, authData.authToken());
        Assertions.assertEquals(2, serverFacade.listGames(authData.authToken()).length);
    }

    @Test
    @DisplayName("negative listGame test")
    public void listGameNotLoggedIn() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames("fake token"));
    }
}