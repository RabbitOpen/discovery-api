package rabbit.discovery.api.common;

public interface DiscoveryService {

    default void setConfiguration(Configuration configuration) {}

    /**
     * 启动
     */
    void start();

    /**
     * 值越小，优先级越高
     * @return
     */
    default int getPriority() {
        return Integer.MAX_VALUE;
    }
}
