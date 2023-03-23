package home.serg.chatik.context;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.dao.Entity;
import home.serg.chatik.dao.token.TokenRepository;
import home.serg.chatik.dao.user.UserRepository;

public enum DaoContext {
    USER_REPOSITORY(new UserRepository()),
    TOKEN_REPOSITORY(new TokenRepository());

    private final DAO<?, ?> dao;

    DaoContext(DAO<?, ?> dao) {
        this.dao = dao;
    }

    public DAO<?, ?> getInstance() {
        return dao;
    }
}
