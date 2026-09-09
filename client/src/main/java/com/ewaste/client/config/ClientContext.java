package com.ewaste.client.config;

import com.ewaste.client.session.UserSession;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Service locator and runtime bean container for client singletons:
 * HTTP Client, Jackson ObjectMapper, and UserSession.
 */
public final class ClientContext {

    private static volatile ClientContext instance;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final UserSession session;

    private ClientContext() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(ApiConfig.REQUEST_TIMEOUT_SECONDS))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.session = UserSession.getInstance();
    }

    public static ClientContext getInstance() {
        if (instance == null) {
            synchronized (ClientContext.class) {
                if (instance == null) {
                    instance = new ClientContext();
                }
            }
        }
        return instance;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public UserSession getSession() {
        return session;
    }
}