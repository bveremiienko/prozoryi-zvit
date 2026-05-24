package ua.prozoryvit.transparency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.organizer")
public record DemoUserProperties(
        String email,
        String password
) {
}
