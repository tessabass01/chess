package dataAccess;

import model.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface DataAccess {
    String getUser(String username) throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    Collection<String> listUsers() throws DataAccessException;

    boolean isCorrectPassword(UserData user) throws DataAccessException;

    void delAuth(String authToken) throws DataAccessException;
//
    AuthData getAuth(String authToken) throws DataAccessException;

    int authSize();

    int genGameID();

    boolean checkAuth(String authToken) throws DataAccessException;

    int createGame(String gameName, int gameID) throws DataAccessException;

    //
    default ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    //
//    void createGame() throws DataAccessException;
//
//    void updateGame() throws DataAccessException;
//
    void clearDB() throws DataAccessException;
}
