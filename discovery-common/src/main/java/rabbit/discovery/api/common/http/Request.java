package rabbit.discovery.api.common.http;

import rabbit.discovery.api.common.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * http请求
 */
public abstract class Request {

    private Map<String, String> headers;

    private Map<String, String> pathVariables;

    private Object body;

    private String url;

    public Request(String url, Map<String, String> headers, Map<String, String> pathVariables) {
        this.headers = headers;
        this.pathVariables = pathVariables;
        this.url = url;
    }

    public Request(String url, Map<String, String> headers) {
        this(url, headers, new HashMap<>());
    }

    public Request(String url) {
        this(url, new HashMap<>());
        getHeaders().put("Content-Type", "application/json");
    }

    /**
     * 获取方法类型
     * @return
     */
    protected abstract HttpMethod getMethod();

    /**
     * 获取请求路径
     * @return
     */
    public final String getRequestUrl() {
        String requestUrl = getUrl();
        for (Map.Entry<String, String> entry : pathVariables.entrySet()) {
            requestUrl = requestUrl.replace("{".concat(entry.getKey()).concat("}"), entry.getValue());
        }
        return requestUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

}
