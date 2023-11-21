package rabbit.discovery.api.rest.http;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.rest.HttpClientManager;
import rabbit.discovery.api.rest.anno.ReadTimeout;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.Semaphore;

import static rabbit.discovery.api.common.enums.HttpMethod.*;

/**
 * 异步http client
 */
public class ReactorHttpClientManager extends HttpClientManager<HttpClient.ResponseReceiver> {

    protected ConnectionProvider connectionProvider;

    @Override
    protected void initConnectionManager() {
        connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(getConfiguration().getMaxConnection())
                .maxIdleTime(Duration.ofSeconds(600))
                .maxLifeTime(Duration.ofSeconds(600))
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .pendingAcquireMaxCount(2000)
                .evictInBackground(Duration.ofSeconds(120))
                .build();
    }

    @Override
    protected void doShutdown() {
        if (!connectionProvider.isDisposed()) {
            connectionProvider.disposeLater().block();
            logger.info("http client manager is closed!");
        }
    }

    @Override
    protected final HttpResponse doRequest(HttpRequest requestObj) {
        HttpClient.ResponseReceiver request = getRequestObject(requestObj);
        HttpResponse<Object> response = new HttpResponse<>();
        Mono<String> monoResult = exchange(requestObj, request, response);
        if (requestObj.isAsyncRequest()) {
            response.setData(monoResult);
        } else {
            try {
                Semaphore semaphore = new Semaphore(0);
                monoResult.switchIfEmpty(Mono.defer(() -> {
                    semaphore.release();
                    return Mono.empty();
                })).subscribe(body -> {
                    response.setData(body);
                    semaphore.release();
                });
                semaphore.acquire();
            } catch (Exception e) {
                throw new RestApiException(e);
            }
        }
        return response;
    }

    private Mono<String> exchange(HttpRequest request, HttpClient.ResponseReceiver responseReceiver, HttpResponse response) {
        HttpClient.ResponseReceiver<?> receiver = responseReceiver;
        if (GET != request.getHttpMethod() && null != request.getBody()) {
            String body = getTransformer().transformRequest(request.getMethod(), request.getBody());
            Charset charset = Charset.forName("UTF-8");
            String contentType = request.getContentType();
            String keyWord = "charset=";
            if (contentType.toLowerCase().contains(keyWord)) {
                String charsetName = contentType.substring(contentType.indexOf(keyWord) + keyWord.length());
                charset = Charset.forName(charsetName);
            }
            ByteBufFlux bufFlux = ByteBufFlux.fromString(Mono.just(body), charset, ByteBufAllocator.DEFAULT);
            receiver = ((HttpClient.RequestSender) responseReceiver).send(bufFlux);
        }
        return receiver.responseSingle((resp, content) -> {
            response.setStatusCode(resp.status().code());
            resp.responseHeaders().forEach(entry -> response.setHeader(entry.getKey(), entry.getValue()));
            return content.asByteArray();
        }).map(bytes -> byte2String(unzipIfZipped(response.getHeaders(), bytes)));
    }

    @Override
    protected final void setRequestBody(HttpClient.ResponseReceiver request, String body, String contentType) {
        // ignore 发送请求时再设置
    }

    /**
     * 获取请求对象
     *
     * @param request
     * @return
     */
    protected HttpClient.ResponseReceiver getRequestObject(HttpRequest request) {
        HttpClient httpClient = getHttpClient(request);
        if (GET == request.getHttpMethod()) {
            return httpClient.get().uri(request.getUri());
        }
        if (PUT == request.getHttpMethod()) {
            return httpClient.put().uri(request.getUri());
        }
        if (DELETE == request.getHttpMethod()) {
            return httpClient.delete().uri(request.getUri());
        }
        if (PATCH == request.getHttpMethod()) {
            return httpClient.patch().uri(request.getUri());
        }
        if (POST == request.getHttpMethod()) {
            return httpClient.post().uri(request.getUri());
        }
        throw new RestApiException("unsupported method type: ".concat(request.getHttpMethod().name()));
    }

    protected HttpClient getHttpClient(HttpRequest request) {
        ReadTimeout rt = request.getMethod().getAnnotation(ReadTimeout.class);
        int readTimeout = null == rt ? getConfiguration().getReadTimeout() : rt.value();
        return HttpClient.create(connectionProvider)
                // 1.1.0后该方法会被移除，使用option方法替换，web flux 2.3.9以下使用该方法
                .tcpConfiguration(t -> t.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        getConfiguration().getConnectionTimeout()))
                .responseTimeout(Duration.ofMillis(readTimeout))
                .headers(httpHeaders -> setRequestHeaders(request, httpHeaders));
    }

    /**
     * 设置http请求头
     *
     * @param request
     * @param httpHeaders
     */
    private void setRequestHeaders(HttpRequest request, HttpHeaders httpHeaders) {
        if (!request.hasContentType()) {
            httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
        }
        request.getHeaders().forEach(httpHeaders::set);
    }
}
