package home.serg.chatik.dto;

public class MessageDto {
    private final String name;
    private final String text;

    public MessageDto(String nickname, String message) {
        this.name = nickname;
        this.text = message;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
