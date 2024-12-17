package SudokuMaster;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String sender;
    private String message;
    private boolean isEmoji;

    public ChatMessage(String sender, String message, boolean isEmoji) {
        this.sender = sender;
        this.message = message;
        this.isEmoji = isEmoji;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public boolean isEmoji() {
        return isEmoji;
    }

    @Override
    public String toString() {
        return sender + ": " + message;
    }
}
