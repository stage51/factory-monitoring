package centrikt.factorymonitoring.authserver.utils.iputil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
public class IPUtil {
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "::1";

    public String getClientIp(HttpServletRequest request) {
        log.trace("Entering getClientIp method");

        String ipAddress = extractIpFromHeaders(request);
        log.debug("Extracted IP from headers: {}", ipAddress);

        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            log.debug("IP is empty or unknown, falling back to remote address: {}", ipAddress);
        }

        if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
            ipAddress = getLocalHostIp();
            log.debug("IP is localhost, using local host IP: {}", ipAddress);
        }

        if (!StringUtils.isEmpty(ipAddress) && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
            log.debug("Multiple IPs found, using first one: {}", ipAddress);
        }

        log.trace("Returning client IP: {}", ipAddress);
        return ipAddress;
    }

    private String extractIpFromHeaders(HttpServletRequest request) {
        log.trace("Entering extractIpFromHeaders method");

        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            log.debug("Checking header: {} with value: {}", header, ip);

            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                log.debug("Found valid IP in header {}: {}", header, ip);
                return ip;
            }
        }

        log.info("No valid IP found in headers");
        return null;
    }

    private String getLocalHostIp() {
        log.trace("Entering getLocalHostIp method");

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String localIp = localHost.getHostAddress();
            log.debug("Local host IP: {}", localIp);
            return localIp;
        } catch (UnknownHostException e) {
            log.error("Error fetching local host IP: {}", e.getMessage(), e);
            return LOCALHOST_IPV4;
        }
    }
}

