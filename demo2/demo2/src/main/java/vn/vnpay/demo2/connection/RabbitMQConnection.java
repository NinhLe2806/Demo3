package vn.vnpay.demo2.connection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class RabbitMQConnection {
    private static ObjectPool<Connection> connectionPool;

    public static void initRabbitMQ() {
        try {

            Properties properties = loadProperties();

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getProperty("rabbitmq.host"));
            factory.setPort(Integer.parseInt(properties.getProperty("rabbitmq.port")));
            factory.setUsername(properties.getProperty("rabbitmq.username"));
            factory.setPassword(properties.getProperty("rabbitmq.password"));

            int maxPoolSize = Integer.parseInt(properties.getProperty("rabbitmq.maxPoolSize"));

            GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(maxPoolSize); // set max total objects exist
            poolConfig.setMinIdle(Integer.parseInt(properties.getProperty("rabbitmq.minIdle")));

            connectionPool = new GenericObjectPool<>(new RabbitMQConnectionFactory(factory), poolConfig);

        } catch (Exception e) {
            log.info(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = RabbitMQConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties");) {
            properties.load(inputStream);
        } catch (IOException e) {
            log.info(e.toString());
            e.printStackTrace();
        }
        return properties;
    }

    public static Connection getConnection() throws Exception {
        return connectionPool.borrowObject();
    }

    public static void releaseConnection(Connection connection) {
        try {
            connectionPool.returnObject(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        connectionPool.close();
    }

    private static class RabbitMQConnectionFactory extends BasePooledObjectFactory<Connection> {
        private final ConnectionFactory factory;

        public RabbitMQConnectionFactory(ConnectionFactory factory) {
            this.factory = factory;
        }

        @Override
        public Connection create() throws Exception {
            return factory.newConnection();
        }

        @Override
        public PooledObject<Connection> wrap(Connection connection) {
            return new DefaultPooledObject<>(connection);
        }

        @Override
        public void destroyObject(PooledObject<Connection> p) throws Exception {
            p.getObject().close();
        }
    }
}
