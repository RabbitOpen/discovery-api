package rabbit.discovery.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import rabbit.discovery.api.common.Configuration;
import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.enums.HttpMode;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.rest.facotry.OpenClientFactory;
import rabbit.discovery.api.rest.http.HttpRequest;
import rabbit.discovery.api.rest.http.HttpResponse;
import rabbit.discovery.api.rest.http.SimpleLoadBalancer;
import rabbit.discovery.api.rest.transformer.JsonTransformer;
import rabbit.flt.common.utils.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static rabbit.discovery.api.common.enums.HttpMethod.GET;
import static rabbit.discovery.api.rest.http.MonoHelper.handleAsyncResponse;
import static rabbit.flt.common.utils.ReflectUtils.*;

public abstract class HttpRequestExecutor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

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
     * 负载均衡器
     */
    @Autowired(required = false)
    protected OpenLoadBalancer openLoadBalancer;

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
            requestInterceptor = request -> {
                // do nothing
            };
        }
        if (null == openLoadBalancer) {
            openLoadBalancer = openRequest -> ((OpenClientFactory) openRequest.getClientFactory()).getServerNode();
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
     * @return
     */
    public final Object execute(HttpRequest request) {
        if (GET == request.getHttpMethod() && null != request.getBody()) {
            logger.warn("request body will be ignored when request method is GET");
        }
        // 第一次调用时完成解析
        ServerNode targetServer = getTargetServer(request);
        request.setUri(getServerAddress(targetServer) + request.getUri());
        resolveRequestUri(request);
        getRequestInterceptor().beforeRequest(request);
        return handleResponse(request, clientManager.execute(request, 0));
    }

    /**
     * 处理响应
     *
     * @param request
     * @param response
     * @return
     */
    private Object handleResponse(HttpRequest request, HttpResponse response) {
        Type resultType = request.getResultType();
        if (defaultTypeConverter.containsKey(resultType)) {
            return doDefaultConvert(resultType, response.getData());
        }
        if (request.isAsyncRequest()) {
            return handleAsyncResponse(request, response, (ParameterizedType) resultType, this);
        } else {
            return readResponseByType(request, response, resultType, StringUtils.toString(response.getData()));
        }
    }

    /**
     * 读取响应
     *
     * @param request
     * @param response
     * @param resultType
     * @param body
     * @return
     */
    public Object readResponseByType(HttpRequest request, HttpResponse response, Type resultType, String body) {
        if (request.careResponseHeader()) {
            Type actualType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
            if (defaultTypeConverter.containsKey(actualType)) {
                return new HttpResponse(doDefaultConvert(actualType, body), response.getHeaders());
            }
            Object result = transformer.transformResponse(request.getMethod(), actualType, response.getHeaders(), body);
            return new HttpResponse(result, response.getHeaders());
        } else {
            if (defaultTypeConverter.containsKey(resultType)) {
                return doDefaultConvert(resultType, body);
            }
            return transformer.transformResponse(request.getMethod(), resultType, response.getHeaders(), body);
        }
    }

    private Object doDefaultConvert(Type actualType, Object data) {
        if (null == data) {
            return null;
        }
        return defaultTypeConverter.get(actualType).apply(StringUtils.toString(data));
    }

    private RequestInterceptor getRequestInterceptor() {
        return requestInterceptor;
    }

    /**
     * 解析请求对象
     *
     * @param request
     */
    private void resolveRequestUri(HttpRequest request) {
        request.getPathVariables().forEach((name, value) -> request.setUri(request.getUri().replace("{".concat(name).concat("}"), PathParser.urlEncode(value))));
        StringBuilder sb = new StringBuilder();
        request.getQueryParameters().forEach((name, value) -> {
            if (0 != sb.length()) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append(name).append("=").append(PathParser.urlEncode(value));
        });
        request.setUri(request.getUri().concat(sb.toString()));
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

    protected OpenLoadBalancer getOpenLoadBalancer() {
        return openLoadBalancer;
    }

    /**
     * 初始化转换函数
     */
    protected void initDefaultConverter() {
        defaultTypeConverter.put(String.class, o -> o);
        defaultTypeConverter.put(Integer.class, Integer::parseInt);
        defaultTypeConverter.put(int.class, Integer::parseInt);
        defaultTypeConverter.put(Long.class, Long::parseLong);
        defaultTypeConverter.put(long.class, Long::parseLong);
        defaultTypeConverter.put(Short.class, Short::parseShort);
        defaultTypeConverter.put(short.class, Short::parseShort);
        defaultTypeConverter.put(Boolean.class, Boolean::parseBoolean);
        defaultTypeConverter.put(boolean.class, Boolean::parseBoolean);
        defaultTypeConverter.put(Float.class, Float::parseFloat);
        defaultTypeConverter.put(float.class, Float::parseFloat);
        defaultTypeConverter.put(Double.class, Double::parseDouble);
        defaultTypeConverter.put(double.class, Double::parseDouble);
    }
}
