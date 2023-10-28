package rabbit.discovery.api.common;

public interface DiscoveryService {

    default void setConfiguration(Configuration configuration) {}

    /**
     * 启动
     */
    void start();
}
