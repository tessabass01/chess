package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessPosition;

public class LegalMoves extends UserGameCommand {

    public final int gameID;
    public final ChessPosition startPosition;
    public LegalMoves(int gameID, String authToken, ChessPosition startPosition) {
        super(authToken);
        this.gameID = gameID;
        this.startPosition = startPosition;
        this.commandType = CommandType.LEGAL_MOVES;
    }
}
