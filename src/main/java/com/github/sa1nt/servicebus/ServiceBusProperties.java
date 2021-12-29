package com.github.sa1nt.servicebus;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "az.servicebus")
public record ServiceBusProperties(
        String connectionString,
        Duration idleTimeout,
        String topicClientId
) { }
