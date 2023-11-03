package rabbit.discovery.api.test;

import rabbit.discovery.api.rest.RequestInterceptor;
import rabbit.discovery.api.rest.http.HttpRequest;

public class HttpRequestInterceptor implements RequestInterceptor {

    public static final String INTERCEPTOR_HEADER = "interceptor_header";

    public static final String INTERCEPTOR_VALUE = "sample";

    @Override
    public void beforeRequest(HttpRequest request) {
        request.setHeader(INTERCEPTOR_HEADER, INTERCEPTOR_VALUE);
    }
}
