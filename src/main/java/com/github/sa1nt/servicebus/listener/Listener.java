package com.github.sa1nt.servicebus.listener;

import com.github.sa1nt.servicebus.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {
    protected final Log logger = LogFactory.getLog(getClass());

    @JmsListener(
            destination = "testtopic",
            subscription = "jsonsub",
            containerFactory = "topicJmsListenerContainerFactory"
    )
    public void receiveMessage(User user) {
        logger.info(user);
        System.out.println("Received <" + user + ">");
    }
}
