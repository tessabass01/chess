package dataAccess;

import model.*;

import java.util.Collection;
import java.util.Random;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> UserDict = new HashMap<String, UserData>();
    private final HashMap<String, AuthData> AuthDict = new HashMap<String, AuthData>();
    private final HashMap<String, GameData> GameDict = new HashMap<String, GameData>();


    public String getUser(String username) {
        if (UserDict.containsKey(username)) {
            return username;
        } else {
            return null;
        }
    }


    public void createUser(String username, String password, String email) {
        if (getUser(username) == null) {
            var user = new UserData(username, password, email);
            UserDict.put(username, user);
        }
    }

    public Collection<String> listUsers() {
        return UserDict.keySet();
    }

    public boolean isCorrectPassword(UserData user) {
        if (UserDict.get(user.username()).password() == user.password()) {
            return true;
        } else { return false; }
    }
    public String genAuth() {
        String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder authToken = new StringBuilder();
        Random rnd = new Random();
        while (authToken.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * possible.length());
            authToken.append(possible.charAt(index));
        }
        return authToken.toString();
    }

        public String createAuth(String username){
            String authToken = genAuth();
            var auth = new AuthData(authToken, username);
            AuthDict.put(authToken, auth);
            return authToken;
        }
//
//            void delAuth () throws DataAccessException;
//
        public AuthData getAuth(String authToken) {
            return AuthDict.getOrDefault(authToken, null);
        }
//
//            Collection<GameData> listGames () throws DataAccessException;
//
//            void createGame () throws DataAccessException;
//
//            void updateGame () throws DataAccessException;

        public void clearDB () {
            UserDict.clear();
            AuthDict.clear();
            GameDict.clear();
        }
}
