package centrikt.factorymonitoring.authserver.configs;

import com.infotrends.in.authenticationserver.security.filters.JWTAuthenticationFilter;
import com.infotrends.in.authenticationserver.security.filters.JWTVerifierFilter;
import com.infotrends.in.authenticationserver.security.services.ApplicationUserDetailsService;
import com.infotrends.in.authenticationserver.services.redis.TokensRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends{


    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ApplicationUserDetailsService applicationUserDetailsService;

    @Autowired
    private TokensRedisService redisService;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), redisService))
                .addFilterAfter(new JWTVerifierFilter(redisService), JWTAuthenticationFilter.class)
                .requestMatchers("/api/v1/validateConnection/whitelisted").permitAll()
                .anyRequest()
                .authenticated()
                .and().httpBasic();
        return http.build();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(encoder);
        authenticationProvider.setUserDetailsService(applicationUserDetailsService);

        return authenticationProvider;
    }
}