package vn.vnpay.demo2.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.demo2.connection.RabbitMQChannelPool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitMQProducer {
    private static final String EXCHANGE_NAME = "my_exchange";
    private static final String ROUTING_KEY = "my_routing_key";
    private static final String QUEUE_NAME = "my_queue";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendToRabbitMQ(Object messageObject) throws IOException, TimeoutException {
        String messageJson = convertObjectToJson(messageObject);

        Channel channel = null;
        try {
            channel = RabbitMQChannelPool.getChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            // Regist Queue with QUEUE_NAME
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            // Process sending message to registed queue
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, messageJson.getBytes(StandardCharsets.UTF_8));
            log.info("Sent to RabbitMQ: {}", messageJson);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null) {
                RabbitMQChannelPool.releaseChannel(channel);
            }
        }
    }

    private static String convertObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
}
