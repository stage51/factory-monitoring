package centrikt.factorymonitoring.authserver.configs;

import centrikt.factorymonitoring.authserver.filters.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/favicon.ico", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/auth-server/auth/login", "/api/v1/auth-server/auth/register",
                                "/api/v1/auth-server/auth/refresh-token", "/api/v1/auth-server/auth/logout", "/api/v1/auth-server/auth/forgot", "/api/v1/auth-server/auth/recovery").permitAll()
                        .requestMatchers("/api/v1/auth-server/auth/check", "api/v1/auth-server/help").authenticated()
                        .requestMatchers("/api/v1/auth-server/organizations/profile", "/api/v1/auth-server/users/profile/**").authenticated()
                        .requestMatchers("/api/v1/auth-server/organizations/**", "/api/v1/auth-server/users/**",
                                "api/v1/auth-server/onlines/**", "api/v1/auth-server/refresh_tokens/**",
                                "/api/v1/auth-server/auth/create-api-token").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("ADMIN", "USER", "MANAGER")
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