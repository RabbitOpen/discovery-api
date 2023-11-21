package rabbit.discovery.api.rest.http;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.rest.HttpClientManager;
import rabbit.discovery.api.rest.anno.ReadTimeout;
import rabbit.flt.common.utils.ResourceUtils;
import reactor.core.publisher.Mono;

import java.util.HashMap;

import static rabbit.discovery.api.common.enums.HttpMethod.*;

/**
 * http client 4 连接管理对象
 */
public class HttpClient4Manager extends HttpClientManager<HttpRequestBase> {

    protected HttpClientConnectionManager connectionManager;

    protected CloseableHttpClient httpClient;

    @Override
    protected void initConnectionManager() {
        connectionManager = createConnectionManager();
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    protected PoolingHttpClientConnectionManager createConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        // 最大连接数
        manager.setMaxTotal(getConfiguration().getMaxConnection());
        // 每个路由最大连接数
        manager.setDefaultMaxPerRoute(getConfiguration().getMaxConnectionPerHost());
        return manager;
    }


    @Override
    protected void doShutdown() {
        ResourceUtils.close(httpClient);
        ((PoolingHttpClientConnectionManager) connectionManager).close();
        logger.info("http client manager is closed!");
    }

    @Override
    protected HttpResponse doRequest(HttpRequest requestObj) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(getRequestObject(requestObj));
            HashMap<String, String> headerMap = new HashMap<>();
            for (Header header : response.getAllHeaders()) {
                headerMap.put(header.getName(), header.getValue());
            }
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            Object body;
            if (requestObj.isAsyncRequest()) {
                body = null == bytes ? Mono.empty() : Mono.just(byte2String(unzipIfZipped(headerMap, bytes)));
            } else {
                body = null == bytes ? null : byte2String(unzipIfZipped(headerMap, bytes));
            }
            return new HttpResponse(body, headerMap, response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            throw new RestApiException(e);
        } finally {
            ResourceUtils.close(response);
        }
    }

    @Override
    protected void setRequestBody(HttpRequestBase request, String body, String contentType) {
        if (request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(new StringEntity(body, getContentType(contentType)));
        }
    }

    private ContentType getContentType(String contentType) {
        String[] split = contentType.split(";");
        if (2 == split.length && 2 == split[1].split("=").length) {
            return ContentType.create(split[0].trim(), split[1].split("=")[1].trim());
        }
        return ContentType.APPLICATION_JSON.withCharset("UTF-8");
    }

    /**
     * 获取请求对象
     *
     * @param request
     * @return
     */
    protected HttpRequestBase getRequestObject(HttpRequest request) {
        HttpRequestBase requestBase = getHttpRequestBase(request);
        addRequestBody(request, requestBase);
        request.getHeaders().forEach(requestBase::setHeader);
        ReadTimeout readTimeout = request.getMethod().getAnnotation(ReadTimeout.class);
        RequestConfig config = RequestConfig.custom().setConnectTimeout(getConfiguration().getConnectionTimeout())
                .setSocketTimeout(null == readTimeout ? getConfiguration().getReadTimeout() : readTimeout.value())
                .build();
        requestBase.setConfig(config);
        return requestBase;
    }

    private HttpRequestBase getHttpRequestBase(HttpRequest request) {
        if (GET == request.getHttpMethod()) {
            return new HttpGet(request.getUri());
        }
        if (PUT == request.getHttpMethod()) {
            return new HttpPut(request.getUri());
        }
        if (DELETE == request.getHttpMethod()) {
            return new HttpDelete(request.getUri());
        }
        if (PATCH == request.getHttpMethod()) {
            return new HttpPatch(request.getUri());
        }
        if (POST == request.getHttpMethod()) {
            return new HttpPost(request.getUri());
        }
        throw new RestApiException("unsupported method type: ".concat(request.getHttpMethod().name()));
    }
}
