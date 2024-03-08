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
            return new AuthData("already taken", user.username());
        } else if (user.password() == null) {
            return new AuthData("no password", user.username());
        } else {
            dataAccess.createUser(user);
            var token = dataAccess.createAuth(user.username());
            return new AuthData(token, user.username());
        }
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        return dataAccess.listUsers();
    }

    public int authSize() throws DataAccessException {
        return dataAccess.authSize();
    }
    public AuthData login(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null) {
            return new AuthData("does not exist", user.username());
        } else if (!dataAccess.isCorrectPassword(user)) {
            return new AuthData("wrong password", user.username());
        } else {
            var token = dataAccess.createAuth(user.username());
            return new AuthData(token, user.username());
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
