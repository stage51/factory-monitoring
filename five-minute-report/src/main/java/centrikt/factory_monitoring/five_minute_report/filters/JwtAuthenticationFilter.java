package centrikt.factory_monitoring.five_minute_report.filters;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidTokenException;
import centrikt.factory_monitoring.five_minute_report.utils.jwt.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth-server/auth/login") ||
                path.startsWith("/api/v1/auth-server/auth/register") ||
                path.startsWith("/api/v1/auth-server/auth/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        log.info("JWT Authentication Filter");
        log.info("Processing request: {}", request.getRequestURI());
        if (shouldNotFilter(request)) {
            log.info("Skipping filter for path: {}", request.getRequestURI());
        }
        try {
            if (request.getHeader("x-api-key") != null) {
                log.info("API TOKEN AUTHENTICATION");
                String token = request.getHeader("x-api-key");
                String username = jwtTokenUtil.extractApiUsername(token);
                String role = jwtTokenUtil.extractApiUserRole(token);
                log.info("USERNAME: {}, ROLE: {}", username, role);
                User user = new User(username, "", List.of(new SimpleGrantedAuthority(role)));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else if (request.getHeader("Authorization") != null && request.getHeader("Authorization").startsWith("Bearer ")) {
                log.info("ACCESS TOKEN AUTHENTICATION");
                String token = request.getHeader("Authorization").substring(7);
                String username = jwtTokenUtil.extractUsername(token);
                String role = jwtTokenUtil.extractUserRole(token);
                log.info("USERNAME: {}, ROLE: {}", username, role);
                User user = new User(username, "", List.of(new SimpleGrantedAuthority(role)));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (InvalidTokenException e) {
            log.error("Authentication failed. " + e.getMessage());
        }
        chain.doFilter(request, response);
    }
}