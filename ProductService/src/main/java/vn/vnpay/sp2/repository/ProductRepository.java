package vn.vnpay.sp2.repository;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.sp2.bean.entity.Product;
import vn.vnpay.sp2.connection.PostgresConnection;
import vn.vnpay.sp2.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class ProductRepository {
    private static ProductRepository instance;

    public static ProductRepository getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public int addProduct(Product product) {
        log.info("Begin addProduct...");
        Connection connection = null;
        PreparedStatement statement = null;

        String query = new StringBuilder()
                .append("INSERT INTO product (product_id,product_name, price, created_user) ")
                .append("VALUES (?,?, ?, ?)")
                .toString();
        try {
            connection = PostgresConnection.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getProductName());
            statement.setDouble(3, product.getPrice());
            statement.setString(4, product.getCreatedUser());
            //todo: log record affected
            int successRecords = statement.executeUpdate();
            log.info("There's {} record insert in addProduct", successRecords);
            return successRecords;
        } catch (SQLException e) {
            log.error("Error in addProduct:", e);
            return 0;
        } finally {
            DBUtil.close(statement, connection);
        }
    }
}
