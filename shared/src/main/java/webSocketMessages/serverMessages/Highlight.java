package webSocketMessages.serverMessages;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Highlight extends ServerMessage {

    public final ChessGame game;
    public final ChessGame.TeamColor playerColor;
    public final ChessPosition startPosition;
    public final Collection<ChessMove> legalMoves;

    public Highlight(ChessGame game, ChessGame.TeamColor playerColor, ChessPosition startPosition, Collection legalMoves) {
        super(ServerMessageType.HIGHLIGHT);
        this.game = game;
        this.playerColor = playerColor;
        this.startPosition = startPosition;
        this.legalMoves = legalMoves;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
