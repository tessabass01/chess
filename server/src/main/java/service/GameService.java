package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        if (!dataAccess.checkAuth(authToken)) {
            return new GameData(-1, null, null, "not logged in", null);
        } else {
            var ID = dataAccess.genGameID();
            dataAccess.createGame(gameName, ID);
            return new GameData(ID, "", "", gameName, new ChessGame());
        }
    }

    public HashMap<String, ArrayList<GameData>> listGames(String authToken) throws DataAccessException {
        var response = new HashMap<String, ArrayList<GameData>>();
        response.put("games", null);
        if (!dataAccess.checkAuth(authToken)) {
            return response;
        } else {
            var gamesList = dataAccess.listGames();
            response.put("games", gamesList);
            return response;
        }
    }
}
