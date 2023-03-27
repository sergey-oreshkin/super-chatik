package home.serg.chatik.dao.message;

import home.serg.chatik.dao.Entity;
import home.serg.chatik.dao.user.User;

public class Message extends Entity<Long> {
    private String message;
    private User user;

    public Message() {
    }

    public Message(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
