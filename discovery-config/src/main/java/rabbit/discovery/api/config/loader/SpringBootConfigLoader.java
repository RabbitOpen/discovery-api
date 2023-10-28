package rabbit.discovery.api.config.loader;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import rabbit.discovery.api.common.Framework;
import rabbit.discovery.api.common.RemoteConfig;
import rabbit.discovery.api.config.ConfigLoader;
import rabbit.discovery.api.config.PropertyHandler;
import rabbit.discovery.api.config.context.InjectType;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class SpringBootConfigLoader extends ConfigLoader {

    private ConfigurableEnvironment environment;

    @Override
    public Framework getCompatibleFramework() {
        return Framework.SPRING_BOOT;
    }

    @Override
    public String readProperty(String propertyName) {
        return environment.getProperty(propertyName);
    }

    @Override
    protected String readProperty(String propertyName, String defaultValue) {
        return environment.getProperty(propertyName, defaultValue);
    }

    @Override
    public void addPropertySources(List<RemoteConfig> configs) {
        PropertyHandler handler = getPropertyHandler();
        configs.sort(Comparator.comparing(RemoteConfig::getPriority));
        configs.forEach(c -> {
            Properties properties = readerCache.get(c.getType()).read(c.getContent(), handler);
            if (c.getPriority() < 0) {
                environment.getPropertySources().addFirst(new PropertiesPropertySource(getConfigName(c), properties));
            } else {
                environment.getPropertySources().addLast(new PropertiesPropertySource(getConfigName(c), properties));
            }
        });
    }

    @Override
    protected void updatePropertySources(List<RemoteConfig> configs) {
        PropertyHandler handler = getPropertyHandler();
        configs.forEach(c -> {
            Properties properties = readerCache.get(c.getType()).read(c.getContent(), handler);
            MutablePropertySources sources = environment.getPropertySources();
            String name = getConfigName(c);
            if (sources.contains(name)) {
                sources.replace(name, new PropertiesPropertySource(name, properties));
            }
        });
        beanMeta.forEach((bean, meta) -> meta.inject(this::readProperty, InjectType.UPDATE));
    }

    public final void setEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;
    }
}
