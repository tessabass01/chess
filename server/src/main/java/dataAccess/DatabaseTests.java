package dataAccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.DataService;
import service.GameService;
import service.UserService;

public class DatabaseTests {

    private final MySqlDataAccess data = new MySqlDataAccess();
    private final UserService uservice = new UserService(data);
    private final DataService dservice = new DataService(data);

    private final GameService gservice = new GameService(data);

    public DatabaseTests() throws Exception {
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dservice.clearDB();
    }

    @Test
    @DisplayName("positive register test")
    void register() throws DataAccessException {
        System.out.print("blah");
    }
}
