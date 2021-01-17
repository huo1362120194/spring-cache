package com.springcache.listener;

import com.springcache.entity.User;
import com.springcache.entity.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserListener {

    private static Logger logger = LoggerFactory.getLogger(UserListener.class);

    @Async //异步
    @EventListener(value = User.class)
    public void userListener(User user) throws InterruptedException {
        Thread.sleep(5000);
        logger.info("user: {}" , user);
    }
}
