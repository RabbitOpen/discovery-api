package rabbit.discovery.api.rest;

import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.HttpResponse;

/**
 * http client manager
 * @param <T>
 */
public abstract class HttpClientManager<T> {

    private HttpTransformer transformer;

    private Configuration configuration;

    /**
     * 执行http请求
     * @param request
     * @return
     */
    public final HttpResponse execute(HttpRequest request) {

        return null;
    }

    public HttpTransformer getTransformer() {
        return transformer;
    }

    public void setTransformer(HttpTransformer transformer) {
        this.transformer = transformer;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {

    }
}
