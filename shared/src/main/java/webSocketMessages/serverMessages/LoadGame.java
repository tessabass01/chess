package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGame extends ServerMessage {

    public final ChessGame game;
    public final ChessGame.TeamColor playerColor;

    public LoadGame(ChessGame game, ChessGame.TeamColor playerColor) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.playerColor = playerColor;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
