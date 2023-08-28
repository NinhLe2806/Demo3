package vn.vnpay.demo3.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo3.connection.PostgreSQLConnection;
import vn.vnpay.demo3.bean.entity.Account;
import vn.vnpay.demo3.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class AccountRepository {
    private static AccountRepository instance;

    public static AccountRepository getInstance() {
        if (Objects.isNull(instance)) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public Account findAccountByUsername(String username) throws SQLException {
        log.info("Begin findAccountByUsername...");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        String query = new StringBuilder("SELECT * FROM accounts WHERE username = ?").toString();

        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            Account account = new Account();

            while (resultSet.next()) {
                account.setUsername(resultSet.getString("username"));
                account.setPassword(resultSet.getString("password"));
                account.setUserId(resultSet.getString("user_id"));
            }
            log.info("findAccountByUsername:{}", account.getUsername());
            return account;
        } catch (SQLException e) {
            log.info("Exception in findAccountByUsername:{}", e.getMessage());
            log.error("Error while findAccountByUsername:", e);
            throw e;
        } finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
    }

    public Account findAccountByUserId(String userId) throws SQLException {
        log.info("Begin findAccountByUserId...");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;


        String query = new StringBuilder("SELECT * FROM accounts WHERE user_id = ?").toString();

        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            Account account = new Account();

            while (resultSet.next()) {
                account.setUserId(resultSet.getString("user_id"));
                account.setUsername(resultSet.getString("username"));
                account.setPassword(resultSet.getString("password"));
            }
            log.info("findAccountByUserId with username:{}", account.getUsername());
            return account;
        } catch (SQLException e) {
            log.info("Exception in findAccountByUserId:{}", e.getMessage());
            log.error("Exception in findAccountByUserId", e);
            throw e;
        } finally {
            DBUtil.close(resultSet, preparedStatement, connection);
        }
    }
}
