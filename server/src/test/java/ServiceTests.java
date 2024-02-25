import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import model.*;
import service.UserService;

import java.util.HashMap;

public class ServiceTests {
    private final UserService service = new UserService(new MemoryDataAccess());

    @Test
    void registerUserTest() throws DataAccessException {
        var expectedAuth = new HashMap<String, AuthData>();
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        var authData = service.registerUser(user);
        var users = service.listUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertTrue(users.contains(username));
    }
}
