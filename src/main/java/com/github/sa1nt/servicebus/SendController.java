package com.github.sa1nt.servicebus;

import org.apache.qpid.jms.JmsTopic;
import org.apache.qpid.jms.message.JmsMessage;
import org.apache.qpid.jms.message.facade.JmsMessageFacade;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsMessageFacade;
import org.apache.qpid.proton.amqp.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SendController {
    private static final Logger logger = LoggerFactory.getLogger(SendController.class);

    private static final JmsTopic TOPIC = new JmsTopic("testtopic");

    private final JmsTemplate jmsTemplate;
    private final JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    public SendController(JmsTemplate jmsTemplate, JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @PostMapping("/queue")
    public User postMessage(@RequestParam String userName) {
        var user = new User(userName);
        logger.info("Sending message {} to {}", user, TOPIC);

        // This makes the "Content Type" System property to stay empty
        // Sets the Custom "content_type" property to "application/json"
/*
        jmsMessagingTemplate.convertAndSend(TOPIC, user, Map.of(MessageHeaders.CONTENT_TYPE, "application/json"));
*/

        // doesn't put any header
/*
        var messageHeaderAccessor = new MessageHeaderAccessor();
        messageHeaderAccessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        Message<User> userMessage = MessageBuilder.withPayload(user)
                .setHeaders(messageHeaderAccessor)
                .build();

        jmsMessagingTemplate.send(TOPIC, userMessage);
*/

        // Sets the Custom "ContentType" property to "application/json"
        //"Content Type" System property stays empty
/*
        jmsMessagingTemplate.convertAndSend(
                TOPIC,
                user,
                m -> MessageBuilder.fromMessage(m)
                        .setHeaderIfAbsent("ContentType", MimeTypeUtils.APPLICATION_JSON_VALUE)
                        .build()
        );
*/

        // puts correct header
        jmsTemplate.convertAndSend(
                TOPIC,
                user,
                m -> {
                    var jmsMessage = (JmsMessage) m;
                    var facade = (AmqpJmsMessageFacade) jmsMessage.getFacade();
                    facade.setContentType(Symbol.valueOf(MimeTypeUtils.APPLICATION_JSON_VALUE));
                    m.setStringProperty("objectType", "User");
                    return m;
                }
        );

        return user;
    }
}
