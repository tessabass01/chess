package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Error extends ServerMessage {

    public final String errorMessage;

    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
