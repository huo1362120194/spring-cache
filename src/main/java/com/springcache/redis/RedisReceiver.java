package com.springcache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * redis消息的接收者
 */
@Component
public class RedisReceiver implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(RedisReceiver.class);
    @Override
    public void onMessage(Message message, byte[] bytes) {
        logger.info("redis receive message:{}",message.toString());
    }
}
