package rabbit.discovery.api.plugins.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.ext.HttpRequest;
import rabbit.discovery.api.plugins.server.HttpAuthenticationFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 传统
 */
public class HttpServletFilter extends HttpAuthenticationFilter implements Ordered, Filter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            authenticate(createRequest((HttpServletRequest) servletRequest));
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (DiscoveryException e) {
            logger.warn(e.getMessage());
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "text/plain;charset=UTF-8");
            response.getWriter().println(e.getMessage());
        }
    }

    private HttpRequest createRequest(HttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, request.getHeader(name));
        }
        HttpRequest httpRequest = new HttpRequest(headers, request.getRequestURI());
        httpRequest.setRemoteHost(request.getRemoteHost());
        httpRequest.setRemotePort(request.getRemotePort());
        httpRequest.setLocalAddress(request.getLocalAddr());
        httpRequest.setLocalPort(request.getLocalPort());
        Map<String, String> queryParameters = new HashMap<>();
        request.getParameterMap().forEach((k, values) -> queryParameters.put(k, values[0]));
        httpRequest.setRequestParameters(queryParameters);
        return httpRequest;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("http servlet authentication filter is created");
    }

    @Override
    public void destroy() {
        // ignore
    }

    @Override
    public int getOrder() {
        return -1000;
    }
}
