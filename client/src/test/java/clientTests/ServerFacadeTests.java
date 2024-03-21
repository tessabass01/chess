package clientTests;

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
    public void clear() throws ResponseException {
        serverFacade.clearDB();
    }


    @Test
    @DisplayName("positive addUser test")
    public void addUser() throws ResponseException {
        var token = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        serverFacade.logout(token);
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
    }

    @Test
    @DisplayName("negative logout test")
    public void logoutFaultyToken() throws ResponseException {
    }

    @Test
    @DisplayName("positive createGame test")
    public void createGame() throws ResponseException {
    }

    @Test
    @DisplayName("negative createGame test")
    public void createGameNotLoggedIn() throws ResponseException {
    }

    @Test
    @DisplayName("positive joinObserver test")
    public void joinObserver() throws ResponseException {
    }

    @Test
    @DisplayName("negative joinObserver test")
    public void joinObserverFaultyID() throws ResponseException {
    }

    @Test
    @DisplayName("positive joinGame test")
    public void joinGame() throws ResponseException {
    }

    @Test
    @DisplayName("negative joinGame test")
    public void joinGameWrongColor() throws ResponseException {
    }

    @Test
    @DisplayName("positive listGame test")
    public void listGame() throws ResponseException {
    }

    @Test
    @DisplayName("negative listGame test")
    public void listGameNotLoggedIn() throws ResponseException {
    }
}