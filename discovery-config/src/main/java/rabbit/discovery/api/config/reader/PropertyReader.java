package rabbit.discovery.api.config.reader;

import rabbit.discovery.api.common.exception.ConfigException;
import rabbit.discovery.api.config.ConfigReader;
import rabbit.discovery.api.config.PropertyHandler;
import rabbit.flt.common.utils.ResourceUtils;
import rabbit.flt.common.utils.StringUtils;

import java.io.StringReader;
import java.util.Properties;

public class PropertyReader implements ConfigReader {

    @Override
    public Properties read(String content, PropertyHandler handler) {
        Properties properties = new Properties();
        if (StringUtils.isEmpty(content)) {
            return properties;
        }
        StringReader reader = new StringReader(content);
        try {
            properties.load(reader);
            properties.forEach((key, value) -> properties.setProperty(StringUtils.toString(key),
                    handler.handle(StringUtils.toString(key), StringUtils.toString(value))));
            return properties;
        } catch (Exception e) {
            throw new ConfigException(e);
        } finally {
            ResourceUtils.close(reader);
        }
    }
}
