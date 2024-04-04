package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public record Notification (Type type, String message) {
    public enum Type {
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
