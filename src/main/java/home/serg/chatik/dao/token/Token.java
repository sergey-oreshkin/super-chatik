package home.serg.chatik.dao.token;

import home.serg.chatik.dao.Entity;
import home.serg.chatik.dao.user.User;

public class Token extends Entity<Long> {
    private String token;
    private User user;

    public Token() {
    }

    public Token(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
