package webSocketMessages.userCommands;

import chess.ChessGame;

public class Redraw extends UserGameCommand {

    public final int gameID;
    public Redraw(int gameID, String authToken) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.REDRAW;
    }
}
