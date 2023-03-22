package home.serg.chatik.context;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.dao.Entity;
import home.serg.chatik.dao.token.TokenRepository;
import home.serg.chatik.dao.user.UserRepository;

public enum DaoContext {
    USER_REPOSITORY(new UserRepository()),
    TOKEN_REPOSITORY(new TokenRepository());

    private final DAO<? extends Entity<?>, ?> dao;

    DaoContext(DAO<? extends Entity<?>, ?> dao) {
        this.dao = dao;
    }

    public DAO<? extends Entity<?>, ?> getInstance() {
        return dao;
    }
}
