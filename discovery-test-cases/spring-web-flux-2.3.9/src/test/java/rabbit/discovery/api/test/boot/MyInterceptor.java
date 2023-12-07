package rabbit.discovery.api.test.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rabbit.discovery.api.common.ext.HttpRequest;
import rabbit.discovery.api.common.ext.Interceptor;

@Component
public class MyInterceptor implements Interceptor {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean intercept(HttpRequest request) {
        logger.info("remote: [{}:{}], local: [{}:{}], hasQueryParameters: {}",
                request.getRemoteHost(), request.getRemotePort(), request.getLocalAddress(),
                request.getLocalPort(), !request.getRequestParameters().isEmpty());
        return false;
    }
}
