package dataAccess;

import chess.ChessGame;
import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final HashMap<String, UserData> UserDict = new HashMap<String, UserData>();
    private final HashMap<String, AuthData> AuthDict = new HashMap<String, AuthData>();
    private final HashMap<String, GameData> GameDict = new HashMap<String, GameData>();
    private int ID = 0;



    public String getUser(String username) {
        if (UserDict.containsKey(username)) {
            return username;
        } else {
            return null;
        }
    }


    public void createUser(UserData user) {
        if (getUser(user.username()) == null) {
            UserDict.put(user.username(), user);
        }
    }

    public ArrayList<UserData> listUsers() {
        return new ArrayList<UserData>(UserDict.values());
    }

    public boolean isCorrectPassword(UserData user) {
        if (Objects.equals(UserDict.get(user.username()).password(), user.password())) {
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
    public void delAuth(String authToken) {
        AuthDict.remove(authToken);
    }
    public AuthData getAuth(String authToken) {
        return AuthDict.getOrDefault(authToken, null);
    }

    public int authSize() {
        return AuthDict.size();
    }

    public boolean checkAuth(String authToken) {
        return AuthDict.containsKey(authToken);
    }

    public int genGameID() {
        this.ID += 1;
        return this.ID;
    }

    public int createGame(String gameName) {
        var gameID = genGameID();
        var game = new GameData(gameID, null, null, gameName, new ChessGame());
        GameDict.put(Integer.toString(gameID), game);
        return gameID;
    }
//
    public ArrayList<GameData> listGames() {
        var gameCollection = GameDict.values();
        return new ArrayList<>(gameCollection);
    }

    public GameData getGame(int gameID) {
        var strID = Integer.toString(gameID);
        return GameDict.getOrDefault(strID, null);
    }

     public String updateGame(int gameID, String username, String color) {
        var strGameID = Integer.toString(gameID);
        if (GameDict.containsKey(strGameID)) {
            var gameData = GameDict.get(strGameID);
            if (Objects.equals(color, "WHITE")) {
                if (gameData.whiteUsername() == null) {
                    GameDict.put(strGameID, new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game()));
                } else {
                    return "already taken";
                }
            } else if (Objects.equals(color, "BLACK")) {
                if (gameData.blackUsername() == null) {
                    GameDict.put(strGameID, new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game()));
                } else {
                    return "already taken";
                }
            }
            return "success";
        } else {
            return "does not exist";
        }
    }

    public String leaveGame(int gameID, ChessGame.TeamColor playerColor) {
        var strGameID = Integer.toString(gameID);
        var gameData = GameDict.get(strGameID);
        if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
            GameDict.put(strGameID, new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game()));
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            GameDict.put(strGameID, new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game()));
        }
        return "success";
    }


    public void clearDB() {
        UserDict.clear();
        AuthDict.clear();
        GameDict.clear();
    }
}
