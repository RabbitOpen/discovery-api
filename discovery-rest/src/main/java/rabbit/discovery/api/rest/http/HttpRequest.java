package rabbit.discovery.api.rest.http;

import rabbit.discovery.api.common.enums.HttpMethod;
import rabbit.discovery.api.rest.ClientFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求
 */
public final class HttpRequest {

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
     * 目标应用分组
     */
    private String applicationGroup;

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

    public HttpRequest(String targetApplication, ClientFactory clientFactory) {
        this.targetApplication = targetApplication;
        this.clientFactory = clientFactory;
    }

    public HttpRequest(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
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

    public String getApplicationGroup() {
        return applicationGroup;
    }

    public void setApplicationGroup(String applicationGroup) {
        this.applicationGroup = applicationGroup;
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

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
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
}
