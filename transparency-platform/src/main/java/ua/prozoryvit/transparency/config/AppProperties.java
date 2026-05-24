package ua.prozoryvit.transparency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String uploadDir,
        int reportOverdueDays,
        String publicBaseUrl
) {
}
