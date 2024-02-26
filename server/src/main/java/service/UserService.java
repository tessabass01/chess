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

    public Object registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) != null) {
            return "already taken";
        } else if (user.password() == null) {
            return "no password";
        } else {
            dataAccess.createUser(user.username(), user.password(), user.email());
            var token = dataAccess.createAuth(user.username());
            return new AuthData(token, user.username());
        }
    }

    public Collection<String> listUsers() throws DataAccessException {
        return dataAccess.listUsers();
    }
    public Object login(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            return "does not exist";
        } else if (!dataAccess.isCorrectPassword(user)) {
            return "wrong password";
        } else {
            var token = dataAccess.createAuth(user.username());
            return new AuthData(token, user.username());
        }
    }
//    public void logout(UserData user) {
//
//    }
}
