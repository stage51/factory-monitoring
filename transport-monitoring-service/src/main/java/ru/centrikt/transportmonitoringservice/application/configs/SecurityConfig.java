package ru.centrikt.transportmonitoringservice.application.configs;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.centrikt.transportmonitoringservice.application.filters.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
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
                .cors().disable()
                .authorizeHttpRequests(
                        authz -> authz
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/favicon.ico", "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "api/v1/transport-monitoring/daily-report/fetch/**",
                                        "api/v1/transport-monitoring/navigation-report/fetch/**",
                                        "api/v1/transport-monitoring/mode-report/fetch/**"
                                ).hasAnyRole("ADMIN", "USER", "MANAGER")
                                .requestMatchers(HttpMethod.POST,
                                        "api/v1/transport-monitoring/navigation-report/navigate/**",
                                        "api/v1/transport-monitoring/navigation-report/find/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                                .requestMatchers(HttpMethod.GET, "api/v1/transport-monitoring/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                                .requestMatchers(HttpMethod.POST,"api/v1/transport-monitoring/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT,"api/v1/transport-monitoring/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"api/v1/transport-monitoring/**").hasRole("ADMIN")
                                .anyRequest().hasAnyRole("ADMIN", "USER", "MANAGER")
                                .and()
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("Access denied: {}", accessDeniedException.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
