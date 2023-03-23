package home.serg.chatik.dao.token;

import home.serg.chatik.dao.DAO;
import home.serg.chatik.dao.user.Role;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.util.ConnectionPool;

import java.sql.*;
import java.util.Optional;

public class TokenRepository implements DAO<Token, Long> {
    @Override
    public Optional<Token> findById(Long id) {
        String sql = "SELECT t.*, u.*, r.name FROM token AS t JOIN users AS u ON u.id=t.user_id JOIN role AS r ON r.id=u.role_id WHERE t.id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            Token token = mapToToken(resultSet);
            return Optional.of(token);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Token save(Token entity) {
        String sql = "INSERT INTO token (token, user_id) VALUES (?,?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, entity.getToken());
            preparedStatement.setLong(2, entity.getUser().getId());
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
    public Token update(Token entity) {
        String sql = "UPDATE token SET token=? WHERE id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, entity.getToken());
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
        String sql = "DELETE FROM token WHERE id=?";
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

    public Optional<Token> getByToken(String token) {
        String sql = "SELECT t.*, u.*, r.name FROM token AS t JOIN users AS u ON u.id=t.user_id JOIN role AS r ON r.id=u.role_id WHERE t.token=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            Token tokenEntity = mapToToken(resultSet);
            return Optional.of(tokenEntity);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<Token> getByUserId(Long userId) {
        String sql = "SELECT t.*, u.*, r.name FROM token AS t JOIN users AS u ON u.id=t.user_id JOIN role AS r ON r.id=u.role_id WHERE t.user_id=?";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) return Optional.empty();
            Token tokenEntity = mapToToken(resultSet);
            return Optional.of(tokenEntity);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Token mapToToken(ResultSet resultSet) throws SQLException {
        Token token = new Token();
        User user = new User();
        user.setId(resultSet.getLong(4));
        user.setUsername(resultSet.getString(5));
        user.setPassword(resultSet.getString(6));
        user.setBlocked(resultSet.getBoolean(8));
        user.setRole(Role.valueOf(resultSet.getString(9)));
        token.setId(resultSet.getLong(1));
        token.setToken(resultSet.getString(2));
        token.setUser(user);
        return token;
    }
}
