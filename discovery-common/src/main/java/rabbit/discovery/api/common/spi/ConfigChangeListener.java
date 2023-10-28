package rabbit.discovery.api.common.spi;

public interface ConfigChangeListener {

    /**
     * 配置变更了
     *
     * @param version 当前版本
     */
    void updateConfig(Long version);
}
