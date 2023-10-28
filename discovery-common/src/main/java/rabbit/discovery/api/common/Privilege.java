package rabbit.discovery.api.common;

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
}
