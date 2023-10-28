package rabbit.discovery.api.config;

import java.util.Properties;

public interface ConfigReader {

    /**
     * 读取配置
     * @param content
     * @param handler
     * @return
     */
    Properties read(String content, PropertyHandler handler);
}
