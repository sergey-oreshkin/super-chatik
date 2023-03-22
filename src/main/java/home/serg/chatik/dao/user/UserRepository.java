package home.serg.chatik.dao.user;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository implements DAO<User, Long> {

    @Override
    public User findById(Long id) {
        try (Connection connection = ConnectionPool.get()) {

        } catch (SQLException ex) {

        }
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

    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }
}
