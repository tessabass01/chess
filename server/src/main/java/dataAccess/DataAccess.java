package dataAccess;

import model.*;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface DataAccess {
    String getUser(String username) throws DataAccessException; //

    void createUser(UserData user) throws DataAccessException; //

    String createAuth(String username) throws DataAccessException;

    ArrayList<UserData> listUsers() throws DataAccessException; //

    boolean isCorrectPassword(UserData user) throws DataAccessException; //

    void delAuth(String authToken) throws DataAccessException; //
//
    AuthData getAuth(String authToken) throws DataAccessException; //

    int authSize() throws DataAccessException;

    boolean checkAuth(String authToken) throws DataAccessException; //

    int createGame(String gameName) throws DataAccessException; //

    default ArrayList<GameData> listGames() throws DataAccessException { //
        return null;
    }

    GameData getGame(int gameID) throws DataAccessException;

    String updateGame(int gameID, String username, String color) throws DataAccessException, SQLException; //

    void clearDB() throws DataAccessException; //
}
