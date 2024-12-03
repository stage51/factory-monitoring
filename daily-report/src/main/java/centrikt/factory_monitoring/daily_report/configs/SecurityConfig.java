package centrikt.factory_monitoring.daily_report.configs;

import centrikt.factory_monitoring.daily_report.filters.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Autowired
    public void setJwtAuthenticationFilter(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers(HttpMethod.POST, "/api/v1/daily-report/positions/fetch/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                                .requestMatchers(HttpMethod.GET, "/api/v1/daily-report/positions/**", "api/v1/daily-report/products/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                                .requestMatchers(HttpMethod.POST,"/api/v1/daily-report/positions/**", "api/v1/daily-report/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"/api/v1/daily-report/positions/**", "api/v1/daily-report/products/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/daily-report/positions/**", "api/v1/daily-report/products/**").hasRole("ADMIN")
                                .anyRequest().hasAnyRole("ADMIN", "USER", "MANAGER")
                                .and()
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("Authentication required: {}", authException.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("Access denied: {}", accessDeniedException.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                });
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
