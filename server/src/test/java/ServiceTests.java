import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import model.*;
import service.*;

import java.util.HashMap;

public class ServiceTests {
    private final UserService uservice = new UserService(new MemoryDataAccess());
    private final DataService dservice = new DataService(new MemoryDataAccess());


    @BeforeEach
    void clear() throws DataAccessException {
        dservice.clearDB();
    }

    @Test
    void registerNewUser() throws DataAccessException {
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        var authData = uservice.registerUser(user);
        var users = uservice.listUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertTrue(users.contains(username));
    }

    @Test
    void registerExistingUser() throws DataAccessException {
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        var user2 = new UserData(username, "goodbye", "hello@goodbye.com");
        var authData = uservice.registerUser(user);
        var authData2 = uservice.registerUser(user2);
        var users = uservice.listUsers();
        Assertions.assertTrue(users.size() < 2);
        Assertions.assertTrue(users.contains(username));
    }
}
