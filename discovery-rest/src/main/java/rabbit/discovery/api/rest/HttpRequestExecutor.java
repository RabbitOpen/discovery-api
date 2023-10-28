package rabbit.discovery.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.enums.HttpMode;
import rabbit.discovery.api.common.exception.RestApiException;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.rest.http.SimpleLoadBalancer;
import rabbit.discovery.api.rest.transformer.JsonTransformer;
import rabbit.flt.common.utils.StringUtils;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static rabbit.discovery.api.common.utils.ReflectUtils.*;

public abstract class HttpRequestExecutor {

    /**
     * 请求转换器
     */
    @Autowired(required = false)
    protected HttpTransformer transformer;

    /**
     * 拦截器
     */
    @Autowired(required = false)
    protected RequestInterceptor requestInterceptor;

    @Autowired
    protected Configuration configuration;

    /**
     * 连接管理器
     */
    protected static HttpClientManager clientManager;

    /**
     * 负载均衡器
     */
    @Autowired(required = false)
    protected LoadBalancer loadBalancer;

    /**
     * 默认类型转换函数
     */
    protected Map<Type, Function<String, Object>> defaultTypeConverter = new HashMap<>();

    @PostConstruct
    public void init() {
        if (null == transformer) {
            transformer = new JsonTransformer();
        }
        if (null == loadBalancer) {
            loadBalancer = new SimpleLoadBalancer(configuration);
        }
        if (null == requestInterceptor) {
            requestInterceptor = request -> {};
        }
        initHttpClientManager();
        initDefaultConverter();
    }

    /**
     * 初始化client manager
     */
    private void initHttpClientManager() {
        synchronized (HttpRequestExecutor.class) {
            if (null == clientManager) {
                clientManager = createHttpClientManager();
            }
        }
    }

    @PreDestroy
    public void close() {
        clientManager.shutdown();
    }

    /**
     * 创建连接管理器
     *
     * @return
     */
    private HttpClientManager createHttpClientManager() {
        if (HttpMode.SYNC == configuration.getHttpMode()) {
            if (hasClass("org.apache.commons.httpclient.HttpClient")) {
                return createManagerByName("rabbit.discovery.api.rest.http.HttpClient3Manager");
            } else {
                return createManagerByName("rabbit.discovery.api.rest.http.HttpClient4Manager");
            }
        } else {
            return createManagerByName("rabbit.discovery.api.rest.http.ReactorHttpClientManager");
        }
    }

    /**
     * 根据类名创建对象
     *
     * @param className
     * @return
     */
    private HttpClientManager createManagerByName(String className) {
        HttpClientManager manager = newInstance(loadClass(className));
        manager.setConfiguration(configuration);
        manager.setTransformer(transformer);
        return manager;
    }

    /**
     * 执行请求
     *
     * @param request
     * @param <T>
     * @return
     */
    public final <T> T execute(HttpRequest request) {
        ServerNode targetServer = getTargetServer(request);
        request.setUri(getServerAddress(targetServer) + request.getUri());
        resolveRequestUri(request);
        getRequestInterceptor().beforeRequest(request);
        return handleResponse(request, clientManager.execute(request));
    }

    /**
     * 处理响应
     * @param request
     * @param response
     * @param <T>
     * @return
     */
    private <T> T handleResponse(HttpRequest request, HttpResponse response) {
        Type resultType = request.getResultType();
        if (defaultTypeConverter.containsKey(resultType)) {
            return (T) defaultTypeConverter.get(resultType).apply(StringUtils.toString(response.getData()));
        }
        if (request.isAsyncRequest()) {
            Mono<String> asyncResult = (Mono<String>) response.getData();
            Type rawType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
            return (T) asyncResult.map(body -> readResponseByType(request, response, rawType, body));
        } else {
            return readResponseByType(request, response, resultType, StringUtils.toString(response.getData()));
        }
    }

    /**
     * 读取响应
     * @param request
     * @param response
     * @param resultType
     * @param body
     * @param <T>
     * @return
     */
    private <T> T readResponseByType(HttpRequest request, HttpResponse response, Type resultType, String body) {
        if (request.careResponseHeader()) {
            Type actualType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
            if (defaultTypeConverter.containsKey(actualType)) {
                Object data = defaultTypeConverter.get(actualType).apply(StringUtils.toString(response.getData()));
                return (T) new HttpResponse(data, response.getHeaders());
            }
            Object result = transformer.transformResponse(request.getMethod(), actualType, response.getHeaders(), body);
            return (T) new HttpResponse(result, response.getHeaders());
        } else {
            return (T) transformer.transformResponse(request.getMethod(), resultType, response.getHeaders(), body);
        }
    }

    private RequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    /**
     * 解析请求对象
     * @param request
     */
    private void resolveRequestUri(HttpRequest request) {
        request.getPathVariables().forEach((name, value) -> request.setUri(request.getUri().replace("{".concat(name).concat("}"), encode(value))));
        StringBuilder sb = new StringBuilder();
        request.getQueryParameters().forEach((name, value) -> {
            if (0 != sb.length()) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append(name).append("=").append(encode(value));
        });
        request.setUri(request.getUri().concat(sb.toString()));
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RestApiException(e);
        }
    }

    /**
     * 获取请求对应的服务节点
     *
     * @param request
     * @return
     */
    protected abstract ServerNode getTargetServer(HttpRequest request);

    /**
     * 获取访问地址
     *
     * @param targetServer
     * @return
     */
    protected abstract String getServerAddress(ServerNode targetServer);

    public Configuration getConfiguration() {
        return configuration;
    }

    protected LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * 初始化转换函数
     */
    protected void initDefaultConverter() {
        defaultTypeConverter.put(String.class, o -> o);
        defaultTypeConverter.put(Integer.class, o -> Integer.parseInt(o));
        defaultTypeConverter.put(int.class, o -> Integer.parseInt(o));
        defaultTypeConverter.put(Long.class, o -> Long.parseLong(o));
        defaultTypeConverter.put(long.class, o -> Long.parseLong(o));
        defaultTypeConverter.put(Short.class, o -> Short.parseShort(o));
        defaultTypeConverter.put(short.class, o -> Short.parseShort(o));
        defaultTypeConverter.put(Boolean.class, o -> Boolean.parseBoolean(o));
        defaultTypeConverter.put(boolean.class, o -> Boolean.parseBoolean(o));
        defaultTypeConverter.put(Float.class, o -> Float.parseFloat(o));
        defaultTypeConverter.put(float.class, o -> Float.parseFloat(o));
        defaultTypeConverter.put(Double.class, o -> Double.parseDouble(o));
        defaultTypeConverter.put(double.class, o -> Double.parseDouble(o));
    }
}
