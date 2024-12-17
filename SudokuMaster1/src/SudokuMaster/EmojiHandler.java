package SudokuMaster;

import java.util.Arrays;
import java.util.List;

public class EmojiHandler {
    private List<String> emojis;

    public EmojiHandler() {
        emojis = Arrays.asList("😊", "😂", "😍", "👍", "🎉", "😢", "😡", "💡", "✔️", "❌");
    }

    public List<String> getEmojis() {
        return emojis;
    }

    public boolean isEmoji(String message) {
        return emojis.contains(message);
    }
}
