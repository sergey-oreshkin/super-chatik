package home.serg.chatik.dao.message;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.dao.user.Role;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageRepository implements DAO<Message, Long> {
    @Override
    public Optional<Message> findById(Long id) {
        String sql = "SELECT m.*, u.*, r.name FROM message AS m JOIN users AS u ON u.id=m.user_id JOIN role AS r ON r.id=u.role_id WHERE t.id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            Message message = mapToMessage(resultSet);
            return Optional.of(message);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Message save(Message entity) {
        String sql = "INSERT INTO message (message, user_id) VALUES (?,?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, entity.getMessage());
            preparedStatement.setLong(2, entity.getUser().getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("Getting generated key is failed");
            entity.setId(generatedKeys.getLong(1));
            return entity;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Message update(Message entity) {
        String sql = "UPDATE message SET message=? WHERE id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, entity.getMessage());
            preparedStatement.setLong(2, entity.getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows < 1) throw new SQLException("Update failed");
            return entity;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM message WHERE id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows >= 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public List<Message> getLastMessages(int count) {
        String sql = "SELECT m.*, u.*, r.name FROM message AS m " +
                "JOIN users AS u ON u.id=m.user_id " +
                "JOIN role AS r ON r.id=u.role_id " +
                "ORDER BY m.id DESC LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, count);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                messages.add(mapToMessage(resultSet));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return messages;
    }

    private Message mapToMessage(ResultSet resultSet) throws SQLException {
        Message message = new Message();
        User user = new User();
        user.setId(resultSet.getLong(4));
        user.setUsername(resultSet.getString(5));
        user.setPassword(resultSet.getString(6));
        user.setBlocked(resultSet.getBoolean(8));
        user.setRole(Role.valueOf(resultSet.getString(9)));
        message.setId(resultSet.getLong(1));
        message.setMessage(resultSet.getString(2));
        message.setUser(user);
        return message;
    }
}
