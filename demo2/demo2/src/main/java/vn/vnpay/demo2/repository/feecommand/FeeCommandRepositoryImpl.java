package vn.vnpay.demo2.repository.feecommand;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.connection.PostgreSQLConnection;
import vn.vnpay.demo2.utils.GenericDA;
import vn.vnpay.demo2.entity.FeeCommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Slf4j
public class FeeCommandRepositoryImpl implements FeeCommandRepository {
    public FeeCommandRepositoryImpl() {
    }

    @Override
    public int addFeeCommand(FeeCommand feeCommand) {
        log.info("Begin addFeeCommand...");
        Connection connection = null;
        PreparedStatement statement = null;

        //TODO: check lại commit và rollback
        String query = new StringBuilder()
                .append("INSERT INTO FEE_COMMAND (ID,COMMAND_CODE, TOTAL_RECORD, TOTAL_FEE, CREATED_USER, CREATED_DATE) ")
                .append("VALUES (?,?, ?, ?, ?, ?)")
                .toString();
        try {
            connection = PostgreSQLConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, feeCommand.getId());
            statement.setString(2, feeCommand.getCommandCode());
            statement.setInt(3, feeCommand.getTotalRecord());
            statement.setDouble(4, feeCommand.getTotalFee());
            statement.setString(5, feeCommand.getCreatedUser());
            statement.setTimestamp(6, Timestamp.valueOf(feeCommand.getCreatedDate()));
            //todo: log record affected
            int successRecords = statement.executeUpdate();
            log.info("There's {} record insert in addFeeCommand", successRecords);
            return successRecords;
        } catch (SQLException e) {
            //TODO: CHECK lại tất cả log info cần chuyển thành error
            log.error("Error while add FeeCommand:", e);
            return 0;
        } finally {
            GenericDA.closeStatement(statement);
            PostgreSQLConnection.closeConnection(connection);
        }
    }
}
