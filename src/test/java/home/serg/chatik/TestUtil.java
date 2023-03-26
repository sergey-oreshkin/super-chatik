package home.serg.chatik;

import home.serg.chatik.dao.user.Role;
import home.serg.chatik.dao.user.User;
import home.serg.chatik.listener.AppListener;
import home.serg.chatik.util.ConnectionPool;

import java.sql.*;

public class TestUtil {
    public static final long DEFAULT_USER_ID = 1L;
    public static final long WRONG_ID = 33L;
    public static final String WRONG_USER_NAME="wrong name";
    public static final String WRONG_PASSWORD = "wrong pass";
    public static final String EXISTING_USER_NAME = "user";
    public static final String EXISTING_USER_PASSWORD = "pass";
    public static final Role EXISTING_USER_ROLE = Role.USER;
    public static final boolean EXISTING_USER_BLOCKED = false;
    public static final User EXISTING_USER = new User(EXISTING_USER_NAME, EXISTING_USER_PASSWORD, EXISTING_USER_ROLE, EXISTING_USER_BLOCKED);
    public static final String DEFAULT_MESSAGE = "some message";
    public static final String BLOCKED_USER_NAME = "blockedUser";
    public static final String BLOCKED_USER_PASSWORD = "pass";
    public static final Role BLOCKED_USER_ROLE = Role.USER;
    public static final User BLOCKED_USER = new User(BLOCKED_USER_NAME, BLOCKED_USER_PASSWORD, BLOCKED_USER_ROLE, true);

    public static void initDbWithOneUser() {
        new AppListener().contextInitialized(null);
        createUserInDb();
    }

    public static void clearDatabase() {
        try (
                Connection connection = ConnectionPool.get();
                Statement statement = connection.createStatement();
        ) {
            String sql = "DROP TABLE message, token, users, role";
            statement.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static void createUserInDb() {
        String sql = "INSERT INTO users (username, password, role_id, blocked) VALUES (?,?,(SELECT id FROM role WHERE name=?),?)";
        try (
                Connection connection = ConnectionPool.get();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, EXISTING_USER.getUsername());
            preparedStatement.setString(2, EXISTING_USER.getPassword());
            preparedStatement.setString(3, EXISTING_USER.getRole().name());
            preparedStatement.setBoolean(4, EXISTING_USER.getBlocked());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) throw new SQLException("Affected rows not equals 1");
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (!generatedKeys.next()) throw new SQLException("Getting generated key is failed");
            EXISTING_USER.setId(generatedKeys.getLong(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
