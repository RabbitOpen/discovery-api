package rabbit.discovery.api.common;

import rabbit.discovery.api.common.enums.HttpMethod;

public class Privilege {

    /**
     * 消费方
     */
    private String consumer;

    /**
     * 服务方
     */
    private String provider;

    /**
     * 路径
     */
    private String path;

    /**
     * 方法类型
     */
    private HttpMethod method;

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
