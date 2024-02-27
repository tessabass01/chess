package service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        if (!dataAccess.checkAuth(authToken)) {
            return new GameData(000, null, null, "not logged in", null);
        } else {
            var ID = dataAccess.genGameID();
            dataAccess.createGame(gameName, ID);
            return new GameData(ID, "", "", gameName, new ChessGame());
        }
    }
}
