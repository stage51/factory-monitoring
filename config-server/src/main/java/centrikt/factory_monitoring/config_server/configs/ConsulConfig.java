package centrikt.factory_monitoring.config_server.configs;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ConsulConfig {

    @Value("${spring.cloud.consul.host}")
    private String consulHost;
    @Value("${spring.cloud.consul.port}")
    private int consulPort;

    @Bean
    @Primary
    public ConsulClient consulClient() {
        return new ConsulClient(consulHost, consulPort);
    }
}
