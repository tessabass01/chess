package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGame extends ServerMessage {

    public final ChessGame game;

    public LoadGame(ChessGame game) {
        super(ServerMessageType.NOTIFICATION);
        this.game = game;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
