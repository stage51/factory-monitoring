package centrikt.factorymonitoring.authserver.filters;

import centrikt.factorymonitoring.authserver.exceptions.InvalidTokenException;
import centrikt.factorymonitoring.authserver.utils.jwt.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UsernamePasswordAuthenticationToken authenticationToken;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String validApiKey = "valid-api-key";
    private final String validAuthHeader = "Bearer valid-access-token";
    private final String username = "user1";
    private final String role = "ROLE_USER";

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authenticationToken);
    }

    @Test
    public void testShouldNotFilter_ShouldReturnTrue_WhenPathStartsWithAuth() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth-server/auth/login");

        // Act
        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testShouldNotFilter_ShouldReturnFalse_WhenPathDoesNotStartWithAuth() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/some/other/path");

        // Act
        boolean result = jwtAuthenticationFilter.shouldNotFilter(request);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testDoFilterInternal_ShouldSetAuthenticationForApiKey() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth-server/auth/another");
        when(request.getHeader("x-api-key")).thenReturn(validApiKey);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(jwtTokenUtil.extractApiUsername(validApiKey)).thenReturn(username);
        when(jwtTokenUtil.extractApiUserRole(validApiKey)).thenReturn(role);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(jwtTokenUtil, times(1)).extractApiUsername(validApiKey);
        verify(jwtTokenUtil, times(1)).extractApiUserRole(validApiKey);
        verify(securityContext, times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ShouldSetAuthenticationForBearerToken() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth-server/auth/another");
        when(request.getHeader("x-api-key")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(validAuthHeader);
        when(jwtTokenUtil.extractUsername("valid-access-token")).thenReturn(username);
        when(jwtTokenUtil.extractUserRole("valid-access-token")).thenReturn(role);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(jwtTokenUtil, times(1)).extractUsername("valid-access-token");
        verify(jwtTokenUtil, times(1)).extractUserRole("valid-access-token");
        verify(securityContext, times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ShouldThrowInvalidTokenException_WhenNoValidHeaders() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth-server/auth/another");
        when(request.getHeader("x-api-key")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ShouldNotSetAuthentication_WhenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth-server/auth/another");
        when(request.getHeader("x-api-key")).thenReturn(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtTokenUtil.extractUsername("invalid-token")).thenThrow(new InvalidTokenException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        verify(chain, never()).doFilter(request, response);
    }
}