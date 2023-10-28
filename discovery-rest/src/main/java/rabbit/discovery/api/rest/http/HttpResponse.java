package rabbit.discovery.api.rest.http;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应
 *
 * @param <T>
 */
public class HttpResponse<T> {

    private T data;

    /**
     * 响应头
     */
    private Map<String, String> headers = new HashMap<>();

    private int statusCode;

    public HttpResponse() {
        this(null, new HashMap<>());
    }

    public HttpResponse(T data, Map<String, String> headers, int statusCode) {
        this.data = data;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public HttpResponse(T data, Map<String, String> headers) {
        this(data, headers, 200);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
