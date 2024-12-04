package centrikt.factorymonitoring.modereport.configs;

import centrikt.factorymonitoring.modereport.filters.JwtAuthenticationFilter;
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
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth-server/auth/login", "/api/v1/auth-server/auth/register",
                                "/api/v1/auth-server/auth/refresh-token", "/api/v1/auth-server/auth/logout").permitAll()
                        .requestMatchers("/api/v1/auth-server/auth/check").authenticated()
                        .requestMatchers("/api/v1/auth-server/organizations/profile", "/api/v1/auth-server/users/profile").hasAnyRole("ADMIN", "USER", "MANAGER")
                        .requestMatchers("/api/v1/auth-server/organizations/**", "/api/v1/auth-server/users/**",
                                "api/v1/auth-server/onlines/**", "api/v1/auth-server/refresh_tokens/**",
                                "/api/v1/auth-server/auth/create-api-token").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("ADMIN", "USER", "MANAGER")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("Authentication required: {}", authException.getMessage());
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
