package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Notification extends ServerMessage {

    public final String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public enum NotificationType {
        JOINED_AS_PLAYER,
        JOINED_AS_OBSERVER,
        MADE_MOVE,
        LEFT_GAME,
        RESIGNED_GAME
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
