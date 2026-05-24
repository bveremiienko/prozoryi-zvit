package ua.prozoryvit.transparency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ua.prozoryvit.transparency.config.AppProperties;
import ua.prozoryvit.transparency.config.DemoUserProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, DemoUserProperties.class})
public class TransparencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransparencyApplication.class, args);
    }
}
