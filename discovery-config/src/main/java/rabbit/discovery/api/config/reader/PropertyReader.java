package rabbit.discovery.api.config.reader;

import rabbit.discovery.api.common.exception.ConfigException;
import rabbit.discovery.api.config.ConfigReader;
import rabbit.discovery.api.config.PropertyHandler;
import rabbit.flt.common.utils.ResourceUtil;
import rabbit.flt.common.utils.StringUtil;

import java.io.StringReader;
import java.util.Properties;

public class PropertyReader implements ConfigReader {

    @Override
    public Properties read(String content, PropertyHandler handler) {
        Properties properties = new Properties();
        if (StringUtil.isEmpty(content)) {
            return properties;
        }
        StringReader reader = new StringReader(content);
        try {
            properties.load(reader);
            properties.forEach((key, value) -> properties.setProperty(StringUtil.toString(key),
                    handler.handle(StringUtil.toString(key), StringUtil.toString(value))));
            return properties;
        } catch (Exception e) {
            throw new ConfigException(e);
        } finally {
            ResourceUtil.close(reader);
        }
    }
}
