package home.serg.chatik.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    public static final String DB_USERNAME_KEY = "db.username";
    public static final String DB_PASSWORD_KEY = "db.password";
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_DRIVER_NAME_KEY = "db.driver-name";
    public static final String DEFAULT_DB_DRIVER_NAME = "org.postgresql.Driver";
    public static final String CONNECTION_POOL_SIZE_KEY = "db.connection-pool.size";
    public static final int DEFAULT_CONNECTION_POOL_SIZE = 5;

    private static BlockingQueue<Connection> pool;


    static {
        loadDriver();
        initConnectionPool();
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesHolder.get(CONNECTION_POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_CONNECTION_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = getNewConnection();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(), new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection, args));
            pool.add(proxyConnection);
        }
    }

    private static Connection getNewConnection() {
        try {
            return DriverManager.getConnection(
                    PropertiesHolder.get(DB_URL_KEY),
                    PropertiesHolder.get(DB_USERNAME_KEY),
                    PropertiesHolder.get(DB_PASSWORD_KEY));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void loadDriver() {
        try {
            String propertiesDriverName = PropertiesHolder.get(DB_DRIVER_NAME_KEY);
            String driverName = propertiesDriverName == null ? DEFAULT_DB_DRIVER_NAME : propertiesDriverName;
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
