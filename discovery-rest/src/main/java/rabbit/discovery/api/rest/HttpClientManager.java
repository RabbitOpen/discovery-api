package rabbit.discovery.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.flt.common.utils.GZipUtils;
import rabbit.flt.common.utils.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * http client manager
 *
 * @param <T>
 */
public abstract class HttpClientManager<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private HttpTransformer transformer;

    private Configuration configuration;

    /**
     * 初始化连接管理器
     */
    protected abstract void initConnectionManager();

    /**
     * 关闭
     */
    protected abstract void doShutdown();

    /**
     * 发送请求
     *
     * @param requestObj 请求包装对象
     * @param request    http client request对象
     * @return
     */
    protected abstract HttpResponse doRequest(HttpRequest requestObj, T request);

    /**
     * 设置请求body
     *
     * @param request
     * @param body
     * @param contentType
     */
    protected abstract void setRequestBody(T request, String body, String contentType);

    /**
     * 获取http client request对象
     *
     * @param request
     * @return
     */
    protected abstract T getRequestObject(HttpRequest request);

    /**
     * 执行http请求
     *
     * @param httpRequest
     * @param retried     已经重试的次数
     * @return
     */
    public final HttpResponse execute(HttpRequest httpRequest, int retried) {
        T request = getRequestObject(httpRequest);
        HttpResponse httpResponse = doRequest(httpRequest, request);
        if (httpRequest.isAsyncRequest()) {
            Mono<String> data = (Mono<String>) httpResponse.getData();
            Mono<String> map = data.map(b -> {
                if (200 != httpResponse.getStatusCode()) {
                    throw new RestApiException(b);
                }
                return b;
            }).onErrorResume(e -> {
                // 异步重试
                if (httpRequest.getMaxRetryTimes() == retried) {
                    return Mono.error(e);
                }
                return (Mono<String>) execute(httpRequest, retried + 1).getData();
            });
            httpResponse.setData(map);
        } else {
            Object body = httpResponse.getData();
            if (200 != httpResponse.getStatusCode()) {
                if (httpRequest.getMaxRetryTimes() == retried) {
                    throw new RestApiException(StringUtils.toString(body));
                }
                // 同步步重试
                return execute(httpRequest, retried + 1);
            }
        }
        return httpResponse;
    }

    /**
     * 添加请求body
     *
     * @param requestObj
     * @param request
     */
    protected void addRequestBody(HttpRequest requestObj, T request) {
        if (null != requestObj.getBody()) {
            String body = transformer.transformRequest(requestObj.getMethod(), requestObj.getBody());
            setRequestBody(request, body, requestObj.getContentType());
        }
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

    /**
     * 设置全局配置
     *
     * @param configuration
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.initConnectionManager();
        logger.info("httpClientManager[{}] is created, maxConnection: {}, maxConnectionPerHost: {}, connectionTimeout: {}, readTimeout: {} ",
                getClass().getSimpleName(), configuration.getMaxConnection(), configuration.getMaxConnectionPerHost(),
                configuration.getConnectionTimeout(), configuration.getReadTimeout());
    }

    /**
     * 关闭管理器
     */
    public synchronized void shutdown() {
        if (null != configuration) {
            this.doShutdown();
            this.configuration = null;
        }
    }

    /**
     * unzip
     *
     * @param headers
     * @param data
     * @return
     */
    protected final byte[] unzipIfZipped(Map<String, String> headers, byte[] data) {
        if (isZipped(headers)) {
            return GZipUtils.decompressIgnoreOriginalLength(data, getStepSize(headers));
        } else {
            return data;
        }
    }

    private int getStepSize(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if ("Content-Length".equalsIgnoreCase(entry.getKey())) {
                return Integer.parseInt(entry.getValue());
            }
        }
        return 1024;
    }

    /**
     * 判断是不是gzip数据
     *
     * @param headers
     * @return
     */
    private boolean isZipped(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if ("Content-Encoding".equalsIgnoreCase(entry.getKey()) &&
                    "gzip".equalsIgnoreCase(entry.getValue())) {
                return true;
            }
        }
        return false;
    }
}
