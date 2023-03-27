package home.serg.chatik.context;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.dao.message.MessageRepository;
import home.serg.chatik.dao.token.TokenRepository;
import home.serg.chatik.dao.user.UserRepository;

public enum DaoContext {
    USER_REPOSITORY(new UserRepository()),
    TOKEN_REPOSITORY(new TokenRepository()),
    MESSAGE_REPOSITORY(new MessageRepository());

    private final DAO<?, ?> dao;

    DaoContext(DAO<?, ?> dao) {
        this.dao = dao;
    }

    public DAO<?, ?> getInstance() {
        return dao;
    }
}
