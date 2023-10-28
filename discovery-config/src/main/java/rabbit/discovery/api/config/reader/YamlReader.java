package rabbit.discovery.api.config.reader;

import org.yaml.snakeyaml.Yaml;
import rabbit.discovery.api.config.ConfigReader;
import rabbit.discovery.api.config.PropertyHandler;
import rabbit.flt.common.utils.ResourceUtils;
import rabbit.flt.common.utils.StringUtils;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

public class YamlReader implements ConfigReader {

    @Override
    public Properties read(String content, PropertyHandler handler) {
        Properties properties = new Properties();
        if (StringUtils.isEmpty(content)) {
            return properties;
        }
        StringReader reader = new StringReader(content);
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(reader);
            map.forEach((key, value) -> setValue2Properties(handler, properties, null, key, value));
            return properties;
        } finally {
            ResourceUtils.close(reader);
        }
    }

    private void setValue2Properties(PropertyHandler handler, Properties properties, String prefix, String key, Object value) {
        String realKey = null == prefix ? key : prefix.concat(".").concat(key);
        if (value instanceof Map) {
            ((Map<String, Object>) value).forEach((k, v) -> setValue2Properties(handler, properties, realKey, k, v));
        } else {
            properties.setProperty(realKey, handler.handle(realKey, StringUtils.toString(value)));
        }
    }
}
