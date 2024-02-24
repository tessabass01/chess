package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            dataAccess.createUser(user.username(), user.password(), user.email());
        }
    }
    public AuthData login(UserData user) {}
    public void logout(UserData user) {}
}
