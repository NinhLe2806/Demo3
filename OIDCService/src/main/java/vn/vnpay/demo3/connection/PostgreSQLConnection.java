package vn.vnpay.demo3.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class PostgreSQLConnection {
    private static BasicDataSource dataSource;

    public static void initializeConnectionPool() {
        try {
            // Read config.properties
            Properties properties = new Properties();
            InputStream inputStream = PostgreSQLConnection.class
                    .getClassLoader()
                    .getResourceAsStream("postgre-config.properties");
            properties.load(inputStream);

            // Init and config connection pool
            dataSource = new BasicDataSource();
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.username"));
            dataSource.setPassword(properties.getProperty("db.password"));
            dataSource.setInitialSize(Integer.parseInt(properties.getProperty("db.pool.initialSize")));
            dataSource.setMaxTotal(Integer.parseInt(properties.getProperty("db.pool.maxTotal")));
            dataSource.setMaxIdle(Integer.parseInt(properties.getProperty("db.pool.maxIdle")));
            dataSource.setMinIdle(Integer.parseInt(properties.getProperty("db.pool.minIdle")));

            log.info("Connection pool initialized successfully.");
        } catch (IOException e) {
            log.error("Error while reading config.properties:", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            log.info("Connected to the database.");
            return connection;
        } catch (SQLException e) {
            log.error("Error while getting connection:", e);
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                log.info("Connection closed successfully.");
            }
        } catch (SQLException e) {
            log.error("Error while closing connection: {}", e.getMessage(), e);
        }
    }
}
