package home.serg.chatik.listener;

import home.serg.chatik.dao.user.Role;
import home.serg.chatik.util.ConnectionPool;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class AppListener implements ServletContextListener {
    public static final String SQL_FILE_NAME = "schema.sql";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        initDb();
        populateRoles();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionPool.closePool();
    }

    private void populateRoles() {
        try (
                Connection connection = ConnectionPool.get();
                Statement statement = connection.createStatement();
        ) {
            for (Role role : Role.values()) {
                String query = String.format("SELECT id FROM role WHERE name='%s'", role.name());
                ResultSet resultSet = statement.executeQuery(query);
                if (resultSet.next()) continue;
                String insertQuery = String.format("INSERT INTO role(name) VALUES('%s')", role.name());
                statement.execute(insertQuery);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    private void initDb() {
        try (
                InputStream inputStream = AppListener.class.getClassLoader().getResourceAsStream(SQL_FILE_NAME);
                Connection connection = ConnectionPool.get();
                Statement statement = connection.createStatement();
        ) {
            if (inputStream == null) return;
            String sql = new String(inputStream.readAllBytes());
            statement.execute(sql);
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
