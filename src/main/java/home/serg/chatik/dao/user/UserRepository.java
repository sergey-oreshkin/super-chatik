package home.serg.chatik.dao.user;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.util.ConnectionPool;

import java.sql.*;
import java.util.Optional;

public class UserRepository implements DAO<User, Long> {

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT u.*, r.name FROM users AS u JOIN role AS r ON r.id=u.role_id WHERE u.id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            User user = mapToUser(resultSet);
            return Optional.of(user);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User save(User entity) {
        String sql = "INSERT INTO users (username, password, role_id, blocked) VALUES (?,?,(SELECT id FROM role WHERE name=?),?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getRole().name());
            preparedStatement.setBoolean(4, entity.getBlocked());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("Getting generated key is failed");
            entity.setId(generatedKeys.getLong(1));
            return entity;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public User update(User entity) {
        String sql = "UPDATE users SET username=?, password=?, role_id=(SELECT id FROM role WHERE name=?), blocked=? WHERE id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, entity.getUsername());
            preparedStatement.setString(2, entity.getPassword());
            preparedStatement.setString(3, entity.getRole().name());
            preparedStatement.setBoolean(4, entity.getBlocked());
            preparedStatement.setLong(5, entity.getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows < 1) throw new SQLException("Update failed");
            return entity;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows < 1) return false;
            return true;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.*, r.name FROM users AS u JOIN role AS r ON r.id=u.role_id WHERE u.username=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            User user = mapToUser(resultSet);
            return Optional.of(user);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setBlocked(resultSet.getBoolean("blocked"));
        user.setRole(Role.valueOf(resultSet.getString("name")));
        return user;
    }
}
