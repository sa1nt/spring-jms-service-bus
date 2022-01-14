package com.github.sa1nt.servicebus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import java.util.Map;

@Configuration
public class ServiceBusConfiguration {

    public static final String TYPE_ID_PROPERTY_NAME = "java_type";

    /**
     * Overrides one defined in com.azure.spring.autoconfigure.jms.NonPremiumServiceBusJMSAutoConfiguration#jmsConnectionFactory()
     * Note that for Premium ASB Tier there exists a com.azure.spring.autoconfigure.jms.PremiumServiceBusJMSAutoConfiguration
     * in com.azure.spring:azure-spring-boot:3.12.0
     */
//    @Bean
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
     * Declare {@link JmsListenerContainerFactory} bean for Azure Service Bus Topic.
     * Copied from com.azure.spring.autoconfigure.jms.AbstractServiceBusJMSAutoConfiguration#topicJmsListenerContainerFactory(org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer, javax.jms.ConnectionFactory)
     * in com.azure.spring:azure-spring-boot:3.12.0
     * @param configurer configure {@link DefaultJmsListenerContainerFactory} with sensible defaults
     * @param connectionFactory configure {@link ConnectionFactory} for {@link JmsListenerContainerFactory}
     * @return {@link JmsListenerContainerFactory} bean
     */
//    @Bean
    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {

        DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        configurer.configure(jmsListenerContainerFactory, connectionFactory);
        jmsListenerContainerFactory.setPubSubDomain(Boolean.TRUE);
//        configureCommonListenerContainerFactory(jmsListenerContainerFactory);
//        configureTopicListenerContainerFactory(jmsListenerContainerFactory);
        return jmsListenerContainerFactory;
    }

    /**
     * Without an explicit Converter bean a Message is sent with application/x-java-serialized-object content-type and
     * very unreadable content
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        var jacksonMessageConverter = new MappingJackson2MessageConverter();
        // use the ObjectMapper bean instead of own one in Converter
        jacksonMessageConverter.setObjectMapper(objectMapper);

        // Default is BYTES, which makes the content-type of messages to be "application/octet-stream"
        // Setting to TEXT makes content-type empty in Service Bus Explorer
        jacksonMessageConverter.setTargetType(MessageType.TEXT);
        jacksonMessageConverter.setTypeIdPropertyName(TYPE_ID_PROPERTY_NAME);
        jacksonMessageConverter.setTypeIdMappings(Map.of(
                "User", User.class
        ));
        return jacksonMessageConverter;
    }
}
