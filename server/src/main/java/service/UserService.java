package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData registerUser(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) != null) {
//            return "already taken";
            throw new DataAccessException("already taken");
        } else if (user.password() == null) {
//            return "no password";
            throw new DataAccessException("no password");
        } else {
            dataAccess.createUser(user);
            var authToken = dataAccess.createAuth(user.username());
            return dataAccess.getAuth(authToken);
        }
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        return dataAccess.listUsers();
    }

    public int authSize() throws DataAccessException {
        return dataAccess.authSize();
    }
    public String login(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            return "does not exist";
        } else if (!dataAccess.isCorrectPassword(user)) {
            return "wrong password";
        } else {
            return dataAccess.createAuth(user.username());
        }
    }
    public String logout(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            return "unauthorized";
        } else {
            dataAccess.delAuth(authToken);
            return "success";
        }
    }
}
