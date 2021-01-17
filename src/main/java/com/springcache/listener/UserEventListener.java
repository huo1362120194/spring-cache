package com.springcache.listener;

import com.springcache.entity.User;
import com.springcache.entity.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private static Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @Async //异步
    @EventListener(value = UserEvent.class)
    public void userEventListener(UserEvent userEvent) throws InterruptedException {
        Thread.sleep(5000);
        Object source = userEvent.getSource();
        if(source instanceof User){
            logger.info("source: {}" , source);
        }
    }
}
