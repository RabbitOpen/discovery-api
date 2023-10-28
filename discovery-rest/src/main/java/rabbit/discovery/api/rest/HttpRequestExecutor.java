package rabbit.discovery.api.rest;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.rest.http.HttpRequest;

public abstract class HttpRequestExecutor {

    /**
     * 执行请求
     * @param request
     * @param <T>
     * @return
     */
    public final <T> T execute(HttpRequest request) {
        return null;
    }

    public Configuration getConfiguration() {
        return null;
    }
 }
