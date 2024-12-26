package centrikt.factorymonitoring.authserver.configs;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateTimeConfig {

    @Getter
    private final static String defaultValue = "UTC+00:00";

}