package ru.centrikt.transportmonitoringservice.application.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "timing")
@RefreshScope
public class TimingConfig {
    private Long greenMonitoringTiming;
    private Long yellowMonitoringTiming;
    private Long redMonitoringTiming;
    private Long greenFiveminuteTiming;
    private Long yellowFiveminuteTiming;
    private Long redFiveminuteTiming;
    private Long greenDailyTiming;
    private Long yellowDailyTiming;
    private Long redDailyTiming;

}
