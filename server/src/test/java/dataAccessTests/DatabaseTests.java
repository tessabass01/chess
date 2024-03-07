package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import org.junit.jupiter.api.*;
import service.DataService;
import service.GameService;
import service.UserService;

public class DatabaseTests {

    private final MySqlDataAccess data = new MySqlDataAccess();

    public DatabaseTests() throws Exception {
    }

    @BeforeEach
    void clear() throws DataAccessException {
        data.clearDB();
    }

    @Test
    @DisplayName("positive createGame test")
    void createGame() throws DataAccessException {
        var gameID = data.createGame("monkeypie");
        var games = data.listGames();
        Assertions.assertSame(games.getFirst().gameID(), gameID);
    }
}
