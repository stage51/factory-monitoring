package centrikt.factorymonitoring.authserver.filters;


import centrikt.factorymonitoring.authserver.exceptions.InvalidTokenException;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
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
                path.startsWith("/api/v1/auth-server/auth/refresh-token") ||
                path.startsWith("/api/v1/auth-server/auth/forgot") ||
                path.startsWith("/api/v1/auth-server/auth/recovery") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/favicon.ico") ||
                path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        log.trace("JWT Authentication Filter");
        log.debug("Processing request {}", request.getRequestURI());
        if (shouldNotFilter(request)) {
            log.debug("Skipping filter for path: {}", request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        try {
            String apiKeyHeader = request.getHeader("x-api-key");
            String authHeader = request.getHeader("Authorization");

            if (apiKeyHeader != null) {
                log.trace("API TOKEN AUTHENTICATION");
                String username = jwtTokenUtil.extractApiUsername(apiKeyHeader);
                String role = jwtTokenUtil.extractApiUserRole(apiKeyHeader);

                setAuthentication(username, role, apiKeyHeader, request);
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                log.trace("ACCESS TOKEN AUTHENTICATION");
                String token = authHeader.substring(7);
                String username = jwtTokenUtil.extractUsername(token);
                String role = jwtTokenUtil.extractUserRole(token);

                setAuthentication(username, role, token, request);
            } else {
                log.debug("No valid authentication header found");
                throw new InvalidTokenException("No valid authentication header found");
            }
        } catch (InvalidTokenException e) {
            log.debug("Authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(String username, String role, String token, HttpServletRequest request) {
        log.info("Setting authentication for user: {}, role: {}", username, role);

        User user = new User(username, "", List.of(new SimpleGrantedAuthority(role)));
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
