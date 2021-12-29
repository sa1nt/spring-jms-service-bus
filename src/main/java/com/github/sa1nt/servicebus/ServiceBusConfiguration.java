package com.github.sa1nt.servicebus;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
public class ServiceBusConfiguration {

    @Bean
    public ConnectionFactory asbConnectionFactory(ServiceBusProperties serviceBusProperties) {
        var serviceBusKey = ServiceBusKey.fromAzConnectionString(serviceBusProperties.connectionString());

        var jmsConnectionFactory = new JmsConnectionFactory(
                serviceBusKey.sharedAccessKeyName(),
                serviceBusKey.sharedAccessKey(),
                serviceBusKey.buildUriWithTimeout(serviceBusProperties.idleTimeout())
        );

        jmsConnectionFactory.setClientID(serviceBusProperties.topicClientId());

        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    /**
     * Without an explicit Converter bean a Message is sent with application/x-java-serialized-object content-type and
     * very unreadable content
     */
    @Bean
    public MessageConverter messageConverter() {
        var jacksonMessageConverter = new MappingJackson2MessageConverter();
        // Default is BYTES, which makes the content-type of messages to be "application/octet-stream"
        // Setting to TEXT makes content-type empty in Service Bus Explorer
        jacksonMessageConverter.setTargetType(MessageType.TEXT);
        return jacksonMessageConverter;
    }
}
