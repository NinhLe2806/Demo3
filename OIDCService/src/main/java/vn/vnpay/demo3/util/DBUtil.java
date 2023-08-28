package vn.vnpay.demo3.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DBUtil {
    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
                log.info("Connection closed successfully.");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing connection:", e);
        }
    }

    public static void close(ResultSet resultSet, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
                log.info("Result Set has closed");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing Result Set: ", e);
        }

        try {
            if (connection != null) {
                connection.close();
                log.info("Connection closed successfully.");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing connection: ", e);
        }
    }

    public static void close(PreparedStatement preparedStatement, Connection connection) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
                log.info("Prepared Statementhas closed");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing Prepared Statement:", e);
        }

        try {
            if (connection != null) {
                connection.close();
                log.info("Connection closed successfully.");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing connection: {}", e.getMessage(), e);
        }
    }

    public static void close(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
                log.info("Result Set has closed");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing Result Set: ", e);
        }

        try {
            if (preparedStatement != null) {
                preparedStatement.close();
                log.info("Prepared Statementhas closed");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing Prepared Statement:", e);
        }

        try {
            if (connection != null) {
                connection.close();
                log.info("Connection closed successfully.");
            }
        } catch (SQLException e) {
            log.info("Exception in DBUtil {}:", e.getMessage());
            log.error("Error while closing connection:", e);
        }
    }
}
