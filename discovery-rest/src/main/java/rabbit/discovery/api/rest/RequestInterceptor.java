package rabbit.discovery.api.rest;


import rabbit.discovery.api.rest.http.HttpRequest;

public interface RequestInterceptor {

    /**
     * 前置拦截
     *
     * @param request
     */
    void beforeRequest(HttpRequest request);
}
