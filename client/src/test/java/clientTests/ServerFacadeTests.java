package clientTests;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


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
    public void addUser() throws ResponseException {
        var message = serverFacade.addUser(new UserData("john", "monkeypie", "donkey@pie.com"));
        System.out.println(message);
        Assertions.assertTrue(true);
    }

}