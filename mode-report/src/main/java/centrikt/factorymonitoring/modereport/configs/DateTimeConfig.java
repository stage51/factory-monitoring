package centrikt.factorymonitoring.modereport.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateTimeConfig {

    private static String defaultValue;


    public static String getDefaultValue() {
        if (defaultValue == null) {
            return "UTC+00:00";
        }
        return defaultValue;
    }
}