import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import passoffTests.testClasses.TestException;
import model.*;
import service.*;

import java.util.HashMap;

public class ServiceTests {
    private final MemoryDataAccess data = new MemoryDataAccess();
    private final UserService uservice = new UserService(data);
    private final DataService dservice = new DataService(data);


    @BeforeEach
    void clear() throws DataAccessException {
        dservice.clearDB();
    }

    @Test
    void clearTest() throws DataAccessException {
        var user1 = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authData1 = uservice.registerUser(user1);
        var user2 = new UserData("hola", "goodbye", "hello@goodbye.com");
        var authData2 = uservice.registerUser(user2);
        var user3 = new UserData("hallo", "goodbye", "hello@goodbye.com");
        var authData3 = uservice.registerUser(user3);
        dservice.clearDB();
        var users = uservice.listUsers();
        Assertions.assertEquals(0, users.size());
    }


        @Test
        @DisplayName("positive register test")
    void registerNewUser() throws DataAccessException {
        var username = "hello";
        var user = new UserData(username, "goodbye", "hello@goodbye.com");
        var authData = uservice.registerUser(user);
        var users = uservice.listUsers();
        Assertions.assertFalse(users.isEmpty());
        Assertions.assertTrue(users.contains(username));
    }

    @Test
    @DisplayName("negative register test")
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

    @Test
    @DisplayName("positive login test")
    void loginExistingUser() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authData = uservice.registerUser(user);
        Assertions.assertEquals(authData.getClass(), AuthData.class);
        var message = uservice.logout(authData.authToken());
        var authData2 = uservice.login(user);
        Assertions.assertNotSame("does not exist", authData2.authToken());
        Assertions.assertNotNull(uservice.listUsers());
        Assertions.assertEquals(1, uservice.listUsers().size());
    }

    @Test
    @DisplayName("negative login test")
    void loginNewUser() throws DataAccessException {
        var user = new UserData("hello", "goodbye", "hello@goodbye.com");
        var authData = uservice.login(user);
        Assertions.assertEquals(0, uservice.listUsers().size());
    }
}
