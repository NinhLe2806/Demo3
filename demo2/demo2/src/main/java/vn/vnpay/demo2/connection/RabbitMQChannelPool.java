package vn.vnpay.demo2.connection;

import com.rabbitmq.client.Channel;
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
public class RabbitMQChannelPool {
    private static ObjectPool<Channel> channelPool;

    public static void initChannelPool() {
        Properties properties = loadProperties();

        GenericObjectPoolConfig<Channel> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(Integer.parseInt(properties.getProperty("channel.pool.maxTotal")));
        poolConfig.setMaxIdle(Integer.parseInt(properties.getProperty("channel.pool.maxIdle")));
        poolConfig.setMinIdle(Integer.parseInt(properties.getProperty("channel.pool.minIdle")));

        channelPool = new GenericObjectPool<>(new ChannelFactory());
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = RabbitMQChannelPool.class
                .getClassLoader()
                .getResourceAsStream("config.properties");) {
            properties.load(inputStream);
        } catch (IOException e) {
            log.info(e.toString());
            e.printStackTrace();
        }
        return properties;
    }

    public static Channel getChannel() throws Exception {
        return channelPool.borrowObject();
    }

    public static void releaseChannel(Channel channel) {
        try {
            channelPool.returnObject(channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        channelPool.close();
    }

    private static class ChannelFactory extends BasePooledObjectFactory<Channel> {
        @Override
        public Channel create() throws Exception {
            return RabbitMQConnection.getConnection().createChannel();
        }

        @Override
        public PooledObject<Channel> wrap(Channel channel) {
            return new DefaultPooledObject<>(channel);
        }

        @Override
        public void destroyObject(PooledObject<Channel> p) throws Exception {
            p.getObject().close();
            RabbitMQConnection.releaseConnection(p.getObject().getConnection());
        }
    }
}

