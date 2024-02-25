package dataAccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    String getUser(String username) throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    String createAuth(String username) throws DataAccessException;

    Collection<String> listUsers() throws DataAccessException;

//    void delAuth() throws DataAccessException;
//
//    AuthData getAuth() throws DataAccessException;
//
//    Collection<GameData> listGames() throws DataAccessException;
//
//    void createGame() throws DataAccessException;
//
//    void updateGame() throws DataAccessException;
//
    void clearDB() throws DataAccessException;
}
