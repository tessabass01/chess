package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        if (!dataAccess.checkAuth(authToken)) {
            throw new DataAccessException("Unauthorized");
        } else {
            return dataAccess.createGame(gameName);
        }
    }

    public HashMap<String, ArrayList<GameData>> listGames(String authToken) throws DataAccessException {
        var response = new HashMap<String, ArrayList<GameData>>();
        response.put("games", null);
        if (dataAccess.checkAuth(authToken)) {
            var gamesList = dataAccess.listGames();
            response.put("games", gamesList);
        }
        return response;
    }

    public String joinGame(String authToken, int gameID, String color) throws DataAccessException, SQLException {
        if (dataAccess.checkAuth(authToken)) {
            var username = dataAccess.getAuth(authToken).username();
            var response = dataAccess.updateGame(gameID, username, color);
            if (Objects.equals(response, "success")) {
                return "success";
            } else if (Objects.equals(response, "does not exist")) {
                return "does not exist";
            } else {
                return "already taken";
            }
        } else {
            return "unauthorized";
        }
    }
}
