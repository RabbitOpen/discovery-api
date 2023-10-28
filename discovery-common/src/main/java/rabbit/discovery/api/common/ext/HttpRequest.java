package rabbit.discovery.api.common.ext;

import rabbit.discovery.api.common.Headers;

import java.util.Map;

public class HttpRequest {

    private Map<String, String> headers;

    private String url;

    public HttpRequest(Map<String, String> headers, String url) {
        this.headers = headers;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * 消费方应用编码
     * @return
     */
    public String getConsumer() {
        return headers.get(Headers.APPLICATION_CODE.toLowerCase());
    }
}
