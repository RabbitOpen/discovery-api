package rabbit.discovery.api.plugins.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.common.exception.DiscoveryException;
import rabbit.discovery.api.common.ext.HttpRequest;
import rabbit.discovery.api.plugins.server.HttpAuthenticationFilter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * web flux 认证过滤器
 */
public class WebFluxFilter extends HttpAuthenticationFilter implements WebFilter {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public WebFluxFilter() {
        logger.info("web flux authentication filter is created");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            authenticate(createRequest(exchange.getRequest()));
            return chain.filter(exchange);
        } catch (DiscoveryException e) {
            logger.warn(e.getMessage());
            ServerHttpResponse response = exchange.getResponse();
            DataBuffer wrap = response.bufferFactory().wrap(e.getMessage().getBytes());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(wrap));
        }
    }

    private HttpRequest createRequest(ServerHttpRequest request) {
        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach((name, value) -> headers.put(name, value.get(0)));
        HttpRequest httpRequest = new HttpRequest(headers, request.getPath().value());
        httpRequest.setRemoteHost(request.getRemoteAddress().getHostName());
        httpRequest.setRemotePort(request.getRemoteAddress().getPort());
        httpRequest.setLocalAddress(request.getLocalAddress().getHostName());
        httpRequest.setLocalPort(request.getLocalAddress().getPort());
        Map<String, String> queryParameters = new HashMap<>();
        httpRequest.setMethod(HttpMethod.valueOf(request.getMethod().name().toUpperCase()));
        request.getQueryParams().forEach((k, values) -> queryParameters.put(k, values.get(0)));
        httpRequest.setRequestParameters(queryParameters);
        return httpRequest;
    }
}
