package home.serg.chatik.dao.user;

import home.serg.chatik.dao.Entity;

public class User extends Entity<Long> {
    private String username;
    private String password;
    private Role role;
    private Boolean blocked;

    public User() {
    }

    public User(String username, String password, Role role, Boolean blocked) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.blocked = blocked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}
