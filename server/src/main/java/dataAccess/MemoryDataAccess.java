package dataAccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> UserDict;
    private HashMap<String, AuthData> AuthDict;
    private HashMap<String, GameData> GameDict;

    public String getUser(String username) {
        if (UserDict.containsKey(username)) {
            return username;
        } else {
            return null;
        }
    }


    void createUser(String username, String password, String email) {
        if (getUser(username) == null) {
            var user = new UserData(username, password, email);
            UserDict.put(username, user);

    String createAuth(String username) {

    }

    void delAuth() throws DataAccessException;

    AuthData getAuth() throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void createGame() throws DataAccessException;

    void updateGame() throws DataAccessException;

    void clearDB() throws DataAccessException;
}
