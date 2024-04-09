package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinObserver extends UserGameCommand {

    private final int gameID;
    public JoinObserver(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
