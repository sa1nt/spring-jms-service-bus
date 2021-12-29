package com.github.sa1nt.servicebus;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public record ServiceBusKey(String host, String sharedAccessKeyName, String sharedAccessKey) {

    private static final String ENDPOINT = "Endpoint";
    private static final String HOST = "host";
    private static final String SAS_KEY_NAME = "SharedAccessKeyName";
    private static final String SAS_KEY = "SharedAccessKey";

    private static final String AMQP_URI_FORMAT = "amqps://%s?amqp.idleTimeout=%d";

    public static ServiceBusKey fromAzConnectionString(String connectionString) {
        String[] segments = connectionString.split(";");
        Map<String, String> hashMap = new HashMap<>();

        for (final String segment : segments) {
            final int indexOfEqualSign = segment.indexOf("=");
            final String key = segment.substring(0, indexOfEqualSign);
            final String value = segment.substring(indexOfEqualSign + 1);
            hashMap.put(key, value);
        }

        final String endpoint = hashMap.get(ENDPOINT);
        final String[] segmentsOfEndpoint = endpoint.split("/");
        final String host = segmentsOfEndpoint[segmentsOfEndpoint.length - 1];
        hashMap.put(HOST, host);

        return new ServiceBusKey(hashMap.get(HOST),
                hashMap.get(SAS_KEY_NAME), hashMap.get(SAS_KEY));
    }

    public String buildUriWithTimeout(Duration timeout) {
        return String.format(AMQP_URI_FORMAT, this.host, timeout.toMillis());
    }
}
