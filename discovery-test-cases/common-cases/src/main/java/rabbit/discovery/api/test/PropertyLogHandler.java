package rabbit.discovery.api.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.config.PropertyHandler;

public class PropertyLogHandler implements PropertyHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String handle(String key, String value) {
        logger.info("读取配置项: key: {}, value: {}", key, value);
        return value;
    }
}
