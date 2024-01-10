package rabbit.discovery.api.common.ext;

import rabbit.discovery.api.common.Headers;
import rabbit.discovery.api.common.enums.HttpMethod;

import java.util.Map;

public class HttpRequest {

    private Map<String, String> headers;

    /**
     * 请求参数
     */
    private Map<String, String> requestParameters;

    private String url;

    /**
     * 远端地址
     */
    private String remoteHost;

    /**
     * 远端端口
     */
    private int remotePort;

    /**
     * 本机地址
     */
    private String localAddress;

    /**
     * 业务端口
     */
    private int localPort;

    private HttpMethod method;

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

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public int getLocalPort() {
        return localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
