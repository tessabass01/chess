package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        System.out.print("blah");
        var gameID = data.createGame("monkeypie");
        data.
    }
}
