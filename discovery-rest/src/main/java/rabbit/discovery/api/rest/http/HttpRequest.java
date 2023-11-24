package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.ServerNode;
import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.ClientFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求
 */
public class HttpRequest {

    private String uri;

    /**
     * 方法类型
     */
    private HttpMethod httpMethod;

    /**
     * 返回值类型
     */
    private Type resultType;

    /**
     * 目标应用集群
     */
    private String applicationCluster;

    /**
     * 接口函数
     */
    private Method method;

    private Object body;

    /**
     * 目标应用
     */
    private String targetApplication;

    /**
     * 请求头
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * path变量
     */
    private Map<String, String> pathVariables = new HashMap<>();

    /**
     * 请求参数
     */
    private Map<String, String> queryParameters = new HashMap<>();

    private ClientFactory clientFactory;

    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 0;

    /**
     * 本次请求的目标服务
     */
    private ServerNode targetServer;

    /**
     * 绑定的附件
     */
    private Map<String, Object> attachments = new HashMap<>();

    public HttpRequest(String targetApplication, ClientFactory clientFactory) {
        this();
        this.targetApplication = targetApplication;
        this.clientFactory = clientFactory;
    }

    public HttpRequest(ClientFactory clientFactory) {
        this(null, clientFactory);
    }

    public HttpRequest() {
    }

    /**
     * 是异步请求
     * @return
     */
    public boolean isAsyncRequest() {
        if (resultType instanceof ParameterizedType) {
            String typeName = ((ParameterizedType) resultType).getRawType().getTypeName();
            return "reactor.core.publisher.Mono".equals(typeName);
        }
        return false;
    }

    /**
     * 判断请求是否关心响应头
     * @return
     */
    public boolean careResponseHeader() {
        if (isAsyncRequest()) {
            Type realType = ((ParameterizedType) resultType).getActualTypeArguments()[0];
            return (realType instanceof ParameterizedType) && ((ParameterizedType) realType).getRawType() == HttpResponse.class;
        } else {
            return (resultType instanceof ParameterizedType) && ((ParameterizedType) resultType).getRawType() == HttpResponse.class;
        }
    }

    /**
     * 获取请求content-type
     * @return
     */
    public String getContentType() {
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            if ("content-type".equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "application/json;charset=UTF-8";
    }

    /**
     * 判断请求是否指定了content-type
     * @return
     */
    public boolean hasContentType() {
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            if ("content-type".equalsIgnoreCase(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Type getResultType() {
        return resultType;
    }

    public void setResultType(Type resultType) {
        this.resultType = resultType;
    }

    public String getApplicationCluster() {
        return applicationCluster;
    }

    public void setApplicationCluster(String applicationCluster) {
        this.applicationCluster = applicationCluster;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public <T> T getBody() {
        return (T) body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getTargetApplication() {
        return targetApplication;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public <T> void addAttachment(String name, T value) {
        this.attachments.put(name, value);
    }

    public <T> T getAttachment(String name) {
        return (T) this.attachments.get(name);
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public ServerNode getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(ServerNode targetServer) {
        this.targetServer = targetServer;
    }
}
