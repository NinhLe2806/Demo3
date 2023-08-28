package vn.vnpay.demo2.repository.feetransaction;


import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.connection.PostgreSQLConnection;
import vn.vnpay.demo2.utils.GenericDA;
import vn.vnpay.demo2.entity.FeeTransaction;
import vn.vnpay.demo2.entity.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class FeeTransactionRepositoryImpl implements FeeTransactionRepository {
    private static final String INSERT_SQL = new StringBuilder()
            .append("INSERT INTO FEE_TRANSACTION ")
            .append("(ID, TRANSACTION_CODE, COMMAND_CODE, FEE_AMOUNT, STATUS, ACCOUNT_NUMBER, ")
            .append("TOTAL_SCAN,REMARK, CREATED_DATE, MODIFIED_DATE) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            .toString();

    private static final String UPDATE_SQL = new StringBuilder()
            .append("UPDATE FEE_TRANSACTION ")
            .append("SET TRANSACTION_CODE = ?, COMMAND_CODE = ?, FEE_AMOUNT = CAST(? AS numeric(18,2)), STATUS = ?,")
            .append("ACCOUNT_NUMBER = ?, TOTAL_SCAN = CAST(? AS integer), REMARK = ?, CREATED_DATE = CAST(? AS TIMESTAMP), ")
            .append("MODIFIED_DATE = CAST(? AS TIMESTAMP) WHERE ID = ? ")
            .toString();

    private static final String SELECT_PROCESSING_TRANSACTIONS = new StringBuilder()
            .append("SELECT * FROM FEE_TRANSACTION WHERE TOTAL_SCAN < 5 ")
            .append("AND STATUS = '02'")
            .toString();

    private static final String SELECT_BY_COMMAND_CODE = "SELECT * FROM FEE_TRANSACTION WHERE COMMAND_CODE = ?";

    private static final String SELECT_BY_TRANS_CODE = "SELECT * FROM FEE_TRANSACTION WHERE TRANSACTION_CODE = ?";
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public int addFeeTransaction(FeeTransaction feeTransaction) {
        log.info("Begin addFeeTransaction...");
        Connection connection = null;
        try {

            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(INSERT_SQL);

            preparedStatement.setString(1, feeTransaction.getId());
            preparedStatement.setString(2, feeTransaction.getTransactionCode());
            preparedStatement.setString(3, feeTransaction.getCommandCode());
            preparedStatement.setDouble(4, feeTransaction.getFeeAmount());
            preparedStatement.setString(5, feeTransaction.getStatus().getCode());
            preparedStatement.setString(6, feeTransaction.getAccountNumber());
            preparedStatement.setInt(7, feeTransaction.getTotalScan());
            preparedStatement.setString(8, feeTransaction.getRemark());
            preparedStatement.setTimestamp(9, Timestamp.valueOf(feeTransaction.getCreatedDate()));
            preparedStatement.setTimestamp(10, Timestamp.valueOf(feeTransaction.getModifiedDate()));

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.info("Error while add FeeTransactions: {}", e.getMessage(), e);
            return 0;
        } finally {
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }

    @Override
    public int addFeeTransactions(List<FeeTransaction> feeTransactions) {
        log.info("Begin addFeeTransactions...");
        Connection connection;
        try {
            connection = PostgreSQLConnection.getConnection();
            connection.setAutoCommit(false); // turn off auto commit
            try {
                preparedStatement = connection.prepareStatement(INSERT_SQL);
                for (FeeTransaction feeTransaction : feeTransactions) {
                    preparedStatement.setString(1, feeTransaction.getId());
                    preparedStatement.setString(2, feeTransaction.getTransactionCode());
                    preparedStatement.setString(3, feeTransaction.getCommandCode());
                    preparedStatement.setDouble(4, feeTransaction.getFeeAmount());
                    preparedStatement.setString(5, feeTransaction.getStatus().getCode());
                    preparedStatement.setString(6, feeTransaction.getAccountNumber());
                    preparedStatement.setInt(7, feeTransaction.getTotalScan());
                    preparedStatement.setString(8, feeTransaction.getRemark());
                    preparedStatement.setTimestamp(9, Timestamp.valueOf(feeTransaction.getCreatedDate()));
                    preparedStatement.setTimestamp(10, Timestamp.valueOf(feeTransaction.getModifiedDate()));

                    preparedStatement.addBatch();
                }
                // exec the batch
                int[] updateRecs = preparedStatement.executeBatch();
                //todo: log before return && log the time to execute
                connection.commit(); // Commit changes since there is no error
                return feeTransactions.size(); // Return the number of inserted records
            } catch (SQLException e) {
                log.info("Error while executing batch insert for FeeTransactions, rolling back...", e);
                connection.rollback();
                return 0;
            } finally {
                connection.setAutoCommit(true); // turn on auto commit
                GenericDA.closeStatement(preparedStatement);
                PostgreSQLConnection.closeConnection(connection);
            }
        } catch (Exception e) {
            //TODO: ĐỂ LUÔN TÊN FUNCTION Ở ERROR LUÔN
            log.error("Error while starting transaction, cannot proceed with addFeeTransactions:", e);
            return 0;
        }
    }

    @Override
    public boolean updateFeeTransaction(FeeTransaction feeTransaction) {
        log.info("Begin updateFeeTransaction...");
        Connection connection = null;
        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_SQL);
            preparedStatement.setString(1, feeTransaction.getTransactionCode());
            preparedStatement.setString(2, feeTransaction.getCommandCode());
            preparedStatement.setDouble(3, feeTransaction.getFeeAmount());
            preparedStatement.setString(4, feeTransaction.getStatus().getCode());
            preparedStatement.setString(5, feeTransaction.getAccountNumber());
            preparedStatement.setInt(6, feeTransaction.getTotalScan());
            preparedStatement.setString(7, feeTransaction.getRemark());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(feeTransaction.getCreatedDate()));
            preparedStatement.setTimestamp(9, Timestamp.valueOf(feeTransaction.getModifiedDate()));
            preparedStatement.setString(10, feeTransaction.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            log.info("There is {} Transaction updated", rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error while starting transaction, cannot proceed with updateFeeTransaction...", e);
            return false;
        } finally {
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }

    @Override
    public int updateFeeTransactions(List<FeeTransaction> feeTransactions) {
        log.info("Begin updateFeeTransactions...");
        Connection connection = null;

        try {
            connection = PostgreSQLConnection.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(UPDATE_SQL);
            for (FeeTransaction feeTransaction : feeTransactions) {
                preparedStatement.setString(1, feeTransaction.getTransactionCode());
                preparedStatement.setString(2, feeTransaction.getCommandCode());
                preparedStatement.setDouble(3, feeTransaction.getFeeAmount());
                preparedStatement.setString(4, feeTransaction.getStatus().getCode());
                preparedStatement.setString(5, feeTransaction.getAccountNumber());
                preparedStatement.setInt(6, feeTransaction.getTotalScan());
                preparedStatement.setString(7, feeTransaction.getRemark());
                preparedStatement.setTimestamp(8, Timestamp.valueOf(feeTransaction.getCreatedDate()));
                preparedStatement.setTimestamp(9, Timestamp.valueOf(feeTransaction.getModifiedDate()));
                preparedStatement.setString(10, feeTransaction.getId());

                preparedStatement.addBatch();
            }
            // exec the updateBatch
            int[] updateCounts = preparedStatement.executeBatch();

            // Calculate the total number of successful updates using Stream
            int successfulUpdates = Arrays.stream(updateCounts)
                                          .filter(updateCount -> updateCount == Statement.SUCCESS_NO_INFO || updateCount > 0)
                                          .sum();
            log.info("There is {} updates success", successfulUpdates);
            return successfulUpdates;
        } catch (SQLException e) {
            log.error("Error while updateFeeTransactions:", e);
            try {
                connection.rollback(); // rollback the entire batch in case of error
            } catch (SQLException ex) {
                log.error("Error while rolling back transaction in updateFeeTransactions:", ex);
            }
            return 0; // return 0 to indicate failure
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // turn on auto commit
                }
            } catch (SQLException e) {
                log.error("Error while setting auto commit to true in updateFeeTransactions:", e);
            }
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }

    @Override
    public FeeTransaction findByTransactionCode(String transactionCode) {
        log.info("Begin findByTransactionCode...");
        Connection connection = null;
        FeeTransaction feeTransaction = new FeeTransaction();

        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BY_TRANS_CODE);
            preparedStatement.setString(1, transactionCode);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                feeTransaction.setId(resultSet.getString("ID"));
                feeTransaction.setTransactionCode(resultSet.getString("TRANSACTION_CODE"));
                feeTransaction.setCommandCode(resultSet.getString("COMMAND_CODE"));
                feeTransaction.setFeeAmount(resultSet.getDouble("FEE_AMOUNT"));
                feeTransaction.setStatus(Status.fromCode(resultSet.getString("STATUS")));
                feeTransaction.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
                feeTransaction.setTotalScan(resultSet.getInt("TOTAL_SCAN"));
                feeTransaction.setRemark(resultSet.getString("REMARK"));
                feeTransaction.setCreatedDate(resultSet.getTimestamp("CREATED_DATE").toLocalDateTime());
                feeTransaction.setModifiedDate(resultSet.getTimestamp("MODIFIED_DATE").toLocalDateTime());
            }
            log.info("FindByTC: {}", feeTransaction.getTransactionCode());
            return feeTransaction;
        } catch (SQLException e) {
            log.error("Error while findByTransactionCode:", e);
            return null;
        } finally {
            GenericDA.closeResultSet(resultSet);
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }

    @Override
    public List<FeeTransaction> getFeeTransactionsInProcess() {
        log.info("Begin getFeeTransactionsInProcess...");
        Connection connection = null;
        List<FeeTransaction> feeTransactions = new ArrayList<>();

        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_PROCESSING_TRANSACTIONS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                FeeTransaction feeTransaction = new FeeTransaction();
                feeTransaction.setId(resultSet.getString("ID"));
                feeTransaction.setTransactionCode(resultSet.getString("TRANSACTION_CODE"));
                feeTransaction.setCommandCode(resultSet.getString("COMMAND_CODE"));
                feeTransaction.setFeeAmount(resultSet.getDouble("FEE_AMOUNT"));
                feeTransaction.setStatus(Status.fromCode(resultSet.getString("STATUS")));
                feeTransaction.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
                feeTransaction.setTotalScan(resultSet.getInt("TOTAL_SCAN"));
                feeTransaction.setRemark(resultSet.getString("REMARK"));
                feeTransaction.setCreatedDate(resultSet.getTimestamp("CREATED_DATE").toLocalDateTime());
                feeTransaction.setModifiedDate(resultSet.getTimestamp("MODIFIED_DATE").toLocalDateTime());

                feeTransactions.add(feeTransaction);
            }
            log.info("Number of records found while scanning: {}", feeTransactions.size());
            return feeTransactions;
        } catch (SQLException e) {
            log.error("Error while getFeeTransactionsInProcess: ", e);
            return null;
        } finally {
            GenericDA.closeResultSet(resultSet);
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }

    @Override
    public List<FeeTransaction> findTransactionByCommandCode(String commandCode) {
        log.info("Begin findTransactionByCommandCode...");
        Connection connection = null;
        List<FeeTransaction> feeTransactions = new ArrayList<>();

        try {
            connection = PostgreSQLConnection.getConnection();
            preparedStatement = connection.prepareStatement(SELECT_BY_COMMAND_CODE);
            preparedStatement.setString(1, commandCode);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FeeTransaction feeTransaction = new FeeTransaction();
                feeTransaction.setId(resultSet.getString("ID"));
                feeTransaction.setTransactionCode(resultSet.getString("TRANSACTION_CODE"));
                feeTransaction.setCommandCode(resultSet.getString("COMMAND_CODE"));
                feeTransaction.setFeeAmount(resultSet.getDouble("FEE_AMOUNT"));
                feeTransaction.setStatus(Status.fromCode(resultSet.getString("STATUS")));
                feeTransaction.setAccountNumber(resultSet.getString("ACCOUNT_NUMBER"));
                feeTransaction.setTotalScan(resultSet.getInt("TOTAL_SCAN"));
                feeTransaction.setRemark(resultSet.getString("REMARK"));
                feeTransaction.setCreatedDate(resultSet.getTimestamp("CREATED_DATE").toLocalDateTime());
                feeTransaction.setModifiedDate(resultSet.getTimestamp("MODIFIED_DATE").toLocalDateTime());

                feeTransactions.add(feeTransaction);
            }
            log.info("????");
            log.info("Number of records found: {}", feeTransactions.size());
            return feeTransactions;
        } catch (SQLException e) {
            log.error("Error while findTransactionByCommandCode:", e);
            return null;
        } finally {
            GenericDA.closeResultSet(resultSet);
            GenericDA.closeStatement(preparedStatement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }
}
