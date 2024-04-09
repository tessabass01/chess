package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Error extends ServerMessage {

    public final String message;

    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
