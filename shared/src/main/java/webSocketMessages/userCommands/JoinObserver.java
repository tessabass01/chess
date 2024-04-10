package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinObserver extends UserGameCommand {

    public JoinObserver(String authToken, String gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
