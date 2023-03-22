package home.serg.chatik;

import home.serg.chatik.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {

        try (Connection connection = ConnectionPool.get()) {

        } catch (SQLException ex) {
        }
    }
}
