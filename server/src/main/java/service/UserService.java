package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            dataAccess.createUser(user.username(), user.password(), user.email());
            var token = dataAccess.createAuth(user.username());
            return new AuthData(token, user.username());
        } else {
            return new AuthData("message", "Error: already taken");
        }
    }

    public Collection<String> listUsers() throws DataAccessException {
        return dataAccess.listUsers();
    }
//    public AuthData login(UserData user) {}
//    public void logout(UserData user) {}
}
