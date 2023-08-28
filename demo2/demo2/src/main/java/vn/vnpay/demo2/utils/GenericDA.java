package vn.vnpay.demo2.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class GenericDA {
    public static void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
                log.info("Result Set has closed");
            }
        } catch (SQLException e) {
            log.info("Error while closing Result Set: {}", e.getMessage(), e);
        }
    }

    public static void closeStatement(PreparedStatement preparedStatement) {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
                log.info("Prepared Statementhas closed");
            }
        } catch (SQLException e) {
            log.info("Error while closing Prepared Statementhas: {}", e.getMessage(), e);
        }
    }

}
