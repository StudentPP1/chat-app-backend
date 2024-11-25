package com.example.websocket_app_test.utils.application;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private List<String> allowedOrigins;
    private String frontEndUrl;
    private String loginSuccessUrl;
}
