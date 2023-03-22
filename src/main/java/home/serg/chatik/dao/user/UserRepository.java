package home.serg.chatik.dao.user;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.util.ConnectionPool;

public class UserRepository implements DAO<User, Long> {

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        return false;
    }
}
